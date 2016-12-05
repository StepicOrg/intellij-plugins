/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.tmp.learning.stepik;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.tmp.learning.StudySerializationUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StepikConnectorGet {
    private static final Logger logger = Logger.getInstance(StepikConnectorGet.class.getName());
    private static final String PYCHARM_PREFIX = "pycharm";
    private static final String CODE_PREFIX = "code";

    static final private Gson GSON =
            new GsonBuilder().registerTypeAdapter(TaskFile.class,
                    new StudySerializationUtils.Json.StepikTaskFileAdapter())
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();


    static <T> T getFromStepik(String link, final Class<T> container) throws IOException {
        return getFromStepik(link, container, StepikConnectorLogin.getHttpClient());
    }

    private static <T> T getFromStepikUnLogin(String link, final Class<T> container) throws IOException {
        return getFromStepik(link, container, StepikConnectorInit.getHttpClient());
    }

    private static <T> T getFromStepik(String link, final Class<T> container, CloseableHttpClient client)
            throws IOException {
        final HttpGet request = new HttpGet(EduStepikNames.STEPIK_API_URL + link);

        final CloseableHttpResponse response = client.execute(request);
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity responseEntity = response.getEntity();
        final String responseString = responseEntity != null ? EntityUtils.toString(responseEntity) : "";
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Stepik returned non 200 status code " + responseString);
        }
        return GSON.fromJson(responseString, container);
    }

    private static <T> T getFromStepik(
            String link,
            final Class<T> container,
            CloseableHttpClient client,
            List<NameValuePair> nvps)
            throws IOException {
        URI uri;
        try {
            uri = new URIBuilder(EduStepikNames.STEPIK_API_URL + link).addParameters(nvps).build();
        } catch (URISyntaxException e) {
            logger.warn(e.getMessage());
            return null;
        }
        final HttpGet request = new HttpGet(uri);

        final CloseableHttpResponse response = client.execute(request);
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity responseEntity = response.getEntity();
        final String responseString = responseEntity != null ? EntityUtils.toString(responseEntity) : "";
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Stepik returned non 200 status code " + responseString);
        }
        return GSON.fromJson(responseString, container);
    }

    @NotNull
    public static List<CourseInfo> getCourses() {
        try {
            List<CourseInfo> result = new ArrayList<>();
            int pageNumber = 1;
            while (addCoursesFromStepik(result, pageNumber++)) ;
            return result;
        } catch (IOException e) {
            logger.error("Cannot load course list " + e.getMessage());
        }
        return Collections.singletonList(CourseInfo.INVALID_COURSE);
    }

    public static List<CourseInfo> getEnrolledCourses() {
        try {
            List<CourseInfo> result = new ArrayList<>();
            int pageNumber = 1;
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("enrolled", "true"));
            while (addCoursesFromStepik(result, pageNumber++, nvps)) ;
            return result;
        } catch (IOException e) {
            logger.error("Cannot load course list " + e.getMessage());
        }
        return Collections.singletonList(CourseInfo.INVALID_COURSE);
    }

    private static boolean addCoursesFromStepik(List<CourseInfo> result, int pageNumber) throws IOException {
        final String url = pageNumber == 0 ?
                EduStepikNames.COURSES :
                EduStepikNames.COURSES_FROM_PAGE + pageNumber;
        final StepikWrappers.CoursesContainer coursesContainer = getFromStepikUnLogin(url,
                StepikWrappers.CoursesContainer.class);
        for (CourseInfo info : coursesContainer.courses) {
            final String courseType = info.getType();
            if (info.isAdaptive() || !StringUtil.isEmptyOrSpaces(courseType)) {
                final List<String> typeLanguage = StringUtil.split(courseType, " ");
                // TODO: should adaptive course be of PyCharmType ?
                if (info.isAdaptive() || (typeLanguage.size() == 2 && PYCHARM_PREFIX.equals(typeLanguage.get(0)))) {
                    for (int instructor : info.instructors) {
                        final StepikUser author =
                                getFromStepikUnLogin(EduStepikNames.USERS + "/" + instructor,
                                        StepikWrappers.AuthorWrapper.class).users.get(0);
                        info.addAuthor(author);
                    }

                    String name = info.getName().replaceAll("[^a-zA-Z0-9\\s]", "");
                    info.setName(name.trim());

                    result.add(info);
                }
            }
        }
        return coursesContainer.meta.containsKey("has_next") && coursesContainer.meta.get("has_next") == Boolean.TRUE;
    }

    private static boolean addCoursesFromStepik(List<CourseInfo> result, int pageNumber, List<NameValuePair> nvps)
            throws IOException {
        final String url = pageNumber == 0 ?
                EduStepikNames.COURSES :
                EduStepikNames.COURSES_FROM_PAGE + pageNumber;
        final StepikWrappers.CoursesContainer coursesContainer =
                getFromStepik(url, StepikWrappers.CoursesContainer.class, StepikConnectorLogin.getHttpClient(), nvps);
        if (coursesContainer == null) {
            return false;
        }
        for (CourseInfo info : coursesContainer.courses) {
            for (int instructor : info.instructors) {
                final StepikUser author =
                        getFromStepik(EduStepikNames.USERS + "/" + instructor,
                                StepikWrappers.AuthorWrapper.class,
                                StepikConnectorInit.getHttpClient()).users.get(0);
                info.addAuthor(author);
            }
            result.add(info);
        }
        return coursesContainer.meta.containsKey("has_next") && coursesContainer.meta.get("has_next") == Boolean.TRUE;
    }

    public static Course getCourse(@NotNull final Project project, @NotNull final CourseInfo info) {
        Course course = new Course();
        course.setName(info.getName());
        course.setAuthors(info.getAuthors());
        course.setDescription(info.getDescription());
        course.setAdaptive(info.isAdaptive());
        course.setId(info.id);
        course.setUpToDate(true);  // TODO: get from stepik

        if (course.isAdaptive()) {
            course = getAdaptiveCourse(project, course);
        } else {
            course = getRegularCourse(course, info);
        }
        return course;
    }

    private static Course getRegularCourse(
            @NotNull Course course,
            @NotNull final CourseInfo info) {
        try {
            for (Integer sectionId : info.sections) {
                List<Lesson> lessons = getLessons(sectionId);
                if (lessons.isEmpty())
                    continue;
                Section section = new Section();
                section.setCourse(course);
                section.setId(sectionId);
                StepikWrappers.Section sectionWrapper = getSection(sectionId);
                if (sectionWrapper != null) {
                    section.setName(sectionWrapper.title);
                } else {
                    section.setName(EduNames.SECTION + sectionId);
                }
                section.addLessons(getLessons(sectionId));
                course.addSectionWithSetIndex(section);
            }
            return course;
        } catch (IOException e) {
            logger.error("IOException " + e.getMessage());
            return null;
        }
    }

    @Nullable
    private static StepikWrappers.Section getSection(int sectionId) {
        StepikWrappers.SectionContainer sectionContainer;
        try {
            sectionContainer = getFromStepik(EduStepikNames.SECTIONS + sectionId,
                    StepikWrappers.SectionContainer.class);
        } catch (IOException e) {
            return null;
        }
        Iterator<StepikWrappers.Section> iterator = sectionContainer.sections.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }

    private static Course getAdaptiveCourse(
            @NotNull final Project project,
            @NotNull Course course) {
        final Lesson lesson = new Lesson();
        lesson.setName("Adaptive");
        Section section = new Section();
        section.setName(lesson.getName());
        section.addLesson(lesson);
        course.addSectionWithSetIndex(section);
        final Task recommendation = EduAdaptiveStepikConnector.getNextRecommendation(project, course);
        if (recommendation != null) {
            lesson.addTask(recommendation);
            return course;
        }

        return null;
    }

    public static List<Lesson> getLessons(int sectionId) throws IOException {
        final StepikWrappers.SectionContainer
                sectionContainer = getFromStepik(EduStepikNames.SECTIONS + sectionId,
                StepikWrappers.SectionContainer.class);
        List<Integer> unitIds = sectionContainer.sections.get(0).units;

        StepikWrappers.UnitContainer
                unitContainer = getFromStepik(EduStepikNames.UNITS + "/" + getIdQuery(unitIds),
                StepikWrappers.UnitContainer.class);
        List<Integer> lessonsIds = new ArrayList<>();
        unitContainer.units.forEach(unit -> lessonsIds.add(unit.lesson));
        StepikWrappers.LessonContainer
                lessonContainer = getFromStepik(EduStepikNames.LESSONS + getIdQuery(lessonsIds),
                StepikWrappers.LessonContainer.class);

        final List<Lesson> lessons = new ArrayList<>();
        for (Lesson lesson : lessonContainer.lessons) {
            createTasks(lesson, lesson.steps);
            if (!lesson.getTaskList().isEmpty()) {
                lessons.add(lesson);
            }
        }

        return lessons;
    }

    private static void createTasks(Lesson lesson, List<Integer> taskIds) throws IOException {
        final StepikWrappers.StepContainer stepContainer = getSteps(taskIds);
        stepContainer.steps.stream().filter(stepSource ->
                supported(stepSource.block.name)).forEach(stepSource -> {
            final Task task = new Task();
            task.setStepId(stepSource.id);
            task.setPosition(stepSource.position);

            switch (stepSource.block.name) {
                case (CODE_PREFIX):
                    createCodeTask(task, stepSource.block);
                    break;
            }
            lesson.addTask(task);
        });
    }

    private static boolean supported(String name) {
        return CODE_PREFIX.equals(name) || PYCHARM_PREFIX.equals(name);
    }


    private static void createCodeTask(Task task, StepikWrappers.Step step) {
        final StringBuilder stringBuilder = new StringBuilder();

        task.setName("step" + task.getPosition());
        stringBuilder.append(step.text).append("<br>");

        if (step.options.samples != null) {
            step.options.samples.stream()
                    .filter(sample -> sample.size() == 2)
                    .forEach(sample -> stringBuilder.append("<b>Sample Input:</b><br>")
                            .append(StringUtil.replace(sample.get(0), "\n", "<br>"))
                            .append("<br>")
                            .append("<b>Sample Output:</b><br>")
                            .append(StringUtil.replace(sample.get(1), "\n", "<br>"))
                            .append("<br><br>"));
        }

        task.setText(stringBuilder.toString());

        setSupportedLang(task, step);

        setTimeLimits(task, step);

        task.getSupportedLanguages().forEach(
                lang -> setTemplate(task, step, lang)
        );
    }

    private static void setTemplate(Task task, StepikWrappers.Step step, SupportedLanguages lang) {
        String templateForTask;
        templateForTask = step.options.codeTemplates.getTemplateForLanguage(lang);
        if (templateForTask != null) {
            final TaskFile taskFile = new TaskFile();
            taskFile.setName(lang.getMainFileName());
            taskFile.setText(templateForTask);
            task.taskFiles.put(taskFile.getName(), taskFile);
        }
    }

    private static void setSupportedLang(Task task, StepikWrappers.Step step) {
        if (step.options.codeTemplates.java8 != null)
            task.addLang(SupportedLanguages.JAVA);
        if (step.options.codeTemplates.python3 != null)
            task.addLang(SupportedLanguages.PYTHON);
    }

    private static void setTimeLimits(Task task, StepikWrappers.Step step) {
        Map<SupportedLanguages, String> timeLimits = new HashMap<>();
        Set<SupportedLanguages> langSet = task.getSupportedLanguages();

        StepikWrappers.LimitsWrapper limits = step.options.limits;
        for (Field field : limits.getClass().getDeclaredFields()) {
            SupportedLanguages curLang = SupportedLanguages.langOf(field.getName());
            if (langSet.contains(curLang)) {
                try {
                    putIfNotNull(timeLimits, curLang, field.get(limits).toString());
                } catch (IllegalAccessException e) {
                    logger.warn(e);
                }
            }
        }
        task.setTimeLimits(timeLimits);
    }

    private static void putIfNotNull(Map<SupportedLanguages, String> timeLimits, SupportedLanguages lang, String limit) {
        if (limit != null && lang != null) {
            timeLimits.put(lang, limit);
        }
    }

    private static StepikWrappers.StepContainer getSteps(List<Integer> steps) throws IOException {
        return getFromStepik(EduStepikNames.STEPS + "/" + getIdQuery(steps), StepikWrappers.StepContainer.class);
    }

    static StepikWrappers.Step getStep(Integer step) throws IOException {
        return getFromStepik(EduStepikNames.STEPS + "/" + String.valueOf(step),
                StepikWrappers.StepContainer.class).steps.get(0).block;
    }

    static StepikWrappers.AuthorWrapper getCurrentUser() {
        try {
            return getFromStepik(EduStepikNames.CURRENT_USER, StepikWrappers.AuthorWrapper.class);
        } catch (IOException e) {
            logger.warn("Couldn't get author info");
        }
        return null;
    }

    public static StepikWrappers.ResultSubmissionWrapper getStatus(int submissionID) {
        final String url = EduStepikNames.SUBMISSIONS + "/" + submissionID;
        try {
            return getFromStepik(url, StepikWrappers.ResultSubmissionWrapper.class);
        } catch (IOException e) {
            logger.warn("Couldn't get Submission");
            return null;
        }
    }

    private static String getIdQuery(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (int id : list) {
            sb.append("ids[]=").append(id).append("&");
        }
        return sb.toString();
    }

    @NotNull
    public static List<Integer> getEnrolledCoursesIds() {
        try {
            final URI enrolledCoursesUri = new URIBuilder(EduStepikNames.COURSES).addParameter("enrolled", "true")
                    .build();
            final List<CourseInfo> courses = getFromStepik(enrolledCoursesUri.toString(),
                    StepikWrappers.CoursesContainer.class).courses;
            return courses.stream().map(CourseInfo::getId).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException | URISyntaxException e) {
            logger.warn(e.getMessage());
        }
        return Collections.emptyList();
    }

    public static StepikWrappers.SubmissionContainer getSubmissions(List<NameValuePair> nvps) {
        try {
            return getFromStepik(EduStepikNames.SUBMISSIONS,
                    StepikWrappers.SubmissionContainer.class,
                    StepikConnectorLogin.getHttpClient(),
                    nvps);
        } catch (IOException e) {
            logger.warn("Can't get submissions\n" + e.getMessage());
            return null;
        }
    }

    public static StepikWrappers.UnitContainer getUnits(String unitId) {
        try {
            return getFromStepik(EduStepikNames.UNITS + "/" + unitId, StepikWrappers.UnitContainer.class);
        } catch (IOException e) {
            logger.warn("Can't get Units\n" + e.getMessage());
            return null;
        }
    }

    public static StepikWrappers.SectionContainer getSections(String sectionId) {
        try {
            return getFromStepik(EduStepikNames.SECTIONS + sectionId, StepikWrappers.SectionContainer.class);
        } catch (IOException e) {
            logger.warn("Can't get Sections\n" + e.getMessage());
            return null;
        }
    }

    public static StepikWrappers.CoursesContainer getCourseInfos(String courseId) {
        try {
            return getFromStepik(EduStepikNames.COURSES + "/" + courseId, StepikWrappers.CoursesContainer.class);
        } catch (IOException e) {
            logger.warn("Can't get courses Info\n" + e.getMessage());
            return null;
        }
    }
}