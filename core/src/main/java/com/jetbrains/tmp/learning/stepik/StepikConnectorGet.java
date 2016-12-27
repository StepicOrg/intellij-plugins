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
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Step;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.stepik.entities.SubmissionContainer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StepikConnectorGet {
    private static final Logger logger = Logger.getInstance(StepikConnectorGet.class.getName());
    private static final String PYCHARM_PREFIX = "pycharm";
    private static final String CODE_PREFIX = "code";
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static <T> T getFromStepik(String link, final Class<T> container) throws IOException {
        return getFromStepik(link, container, StepikConnectorLogin.getHttpClient());
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

    public static Course getCourse(@NotNull final CourseInfo info) {
        Course course = new Course();
        course.setName(info.getName());
        course.setAuthors(info.getAuthors());
        course.setDescription(info.getDescription());
        course.setAdaptive(info.isAdaptive());
        course.setId(info.id);

        if (!course.isAdaptive()) {
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
                    section.setPosition(sectionWrapper.position);
                } else {
                    section.setName(EduNames.SECTION + sectionId);
                }
                section.addLessons(getLessons(sectionId));
                course.addSection(section);
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

    private static List<Lesson> getLessons(int sectionId) throws IOException {
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
        int position = 1;
        for (Lesson lesson : lessonContainer.lessons) {
            createSteps(lesson, lesson.getSteps());
            if (!lesson.getStepList().isEmpty()) {
                lesson.setPosition(position++);
                lessons.add(lesson);
            }
        }

        return lessons;
    }

    private static void createSteps(@NotNull Lesson lesson, List<Integer> stepIds) throws IOException {
        final StepikWrappers.StepContainer stepContainer = getSteps(stepIds);
        stepContainer.steps.stream().filter(stepSource ->
                supported(stepSource.block.name)).forEach(stepSource -> {
            final Step step = new Step();
            step.setId(stepSource.id);
            step.setPosition(stepSource.position);

            switch (stepSource.block.name) {
                case (CODE_PREFIX):
                    createCodeStep(step, stepSource.block);
                    break;
            }
            lesson.addStep(step);
        });
    }

    private static boolean supported(String name) {
        return CODE_PREFIX.equals(name) || PYCHARM_PREFIX.equals(name);
    }


    private static void createCodeStep(@NotNull Step step, @NotNull StepikWrappers.Step stepSource) {
        final StringBuilder stringBuilder = new StringBuilder();

        step.setName("step" + step.getPosition());
        stringBuilder.append(stepSource.text).append("<br>");

        if (stepSource.options.samples != null) {
            stepSource.options.samples.stream()
                    .filter(sample -> sample.size() == 2)
                    .forEach(sample -> stringBuilder.append("<b>Sample Input:</b><br>")
                            .append(StringUtil.replace(sample.get(0), "\n", "<br>"))
                            .append("<br>")
                            .append("<b>Sample Output:</b><br>")
                            .append(StringUtil.replace(sample.get(1), "\n", "<br>"))
                            .append("<br><br>"));
        }

        step.setText(stringBuilder.toString());

        setSupportedLang(step, stepSource);

        setTimeLimits(step, stepSource);

        step.getSupportedLanguages().forEach(
                lang -> setTemplate(step, stepSource, lang)
        );
    }

    private static void setTemplate(
            @NotNull Step step,
            @NotNull StepikWrappers.Step stepSource,
            @NotNull SupportedLanguages lang) {
        String templateForStep;
        templateForStep = stepSource.options.codeTemplates.getTemplateForLanguage(lang);
        if (templateForStep != null) {
            final StepFile stepFile = new StepFile();
            stepFile.setName(lang.getMainFileName());
            stepFile.setText(templateForStep);
            step.getStepFiles().put(stepFile.getName(), stepFile);
        }
    }

    private static void setSupportedLang(@NotNull Step step, @NotNull StepikWrappers.Step stepSource) {
        step.addLanguages(stepSource.options.codeTemplates.getLanguages());
    }

    private static void setTimeLimits(@NotNull Step step, @NotNull StepikWrappers.Step stepSource) {
        Map<SupportedLanguages, String> timeLimits = new HashMap<>();
        List<SupportedLanguages> langList = step.getSupportedLanguages();

        StepikWrappers.LimitsWrapper limits = stepSource.options.limits;

        StepikWrappers.Limit limit;
        for (SupportedLanguages lang : langList) {
            limit = limits.getLimit(lang);
            if (limit != null) {
                timeLimits.put(lang, limit.toString());
            }
        }
        step.setTimeLimits(timeLimits);
    }

    private static StepikWrappers.StepContainer getSteps(List<Integer> steps) throws IOException {
        return getFromStepik(EduStepikNames.STEPS + "/" + getIdQuery(steps), StepikWrappers.StepContainer.class);
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

    public static SubmissionContainer getSubmissions(List<NameValuePair> nvps) {
        try {
            return getFromStepik(EduStepikNames.SUBMISSIONS,
                    SubmissionContainer.class,
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

    @NotNull
    public static List<CourseInfo> getCourses(List<Integer> coursesIds) {
        try {
            if (coursesIds.size() > 20) {
                logger.warn("to match hardcoded courses");
            }
            StepikWrappers.CoursesContainer
                    coursesContainer = getFromStepik(EduStepikNames.COURSES + getIdQuery(coursesIds),
                    StepikWrappers.CoursesContainer.class);
            return coursesContainer.courses;
        } catch (IOException e) {
            logger.warn(e);
        }
        List<CourseInfo> result = Collections.emptyList();
        result.add(CourseInfo.INVALID_COURSE);
        return result;
    }
}