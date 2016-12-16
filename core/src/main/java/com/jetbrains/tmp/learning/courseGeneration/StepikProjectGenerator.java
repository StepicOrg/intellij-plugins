package com.jetbrains.tmp.learning.courseGeneration;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StudySerializationUtils;
import com.jetbrains.tmp.learning.StudySerializationUtils.Json.SupportedLanguagesDeserializer;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import com.jetbrains.tmp.learning.stepik.CourseInfo;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikUser;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jetbrains.tmp.learning.StudyUtils.execCancelable;

public class StepikProjectGenerator {
    public static final File CONFIG_COURSES_DIR = new File(PathManager.getConfigPath(), "courses");

    private static final Logger logger = Logger.getInstance(StepikProjectGenerator.class);
    private static final String AUTHOR_ATTRIBUTE = "authors";
    private static final String LANGUAGE_ATTRIBUTE = "language";
    private static final String COURSE_NAME_ATTRIBUTE = "name";
    private static final String COURSE_DESCRIPTION = "description";
    private static final String CACHE_NAME = "enrolledCourseNames.txt";
    @NotNull
    private SupportedLanguages defaultLang = SupportedLanguages.INVALID;
    @NotNull
    private List<CourseInfo> courses = new ArrayList<>();
    private CourseInfo selectedCourseInfo;

    private static StepikProjectGenerator instance;

    private StepikProjectGenerator(){}

    public static StepikProjectGenerator getInstance() {
        if (instance == null){
            instance = new StepikProjectGenerator();
        }
        return instance;
    }

    /**
    *    Non-static methods
    */

    public void setCourses(@NotNull List<CourseInfo> courses) {
        this.courses = courses;
    }

    public void setSelectedCourse(@Nullable CourseInfo courseInfo) {
        if (courseInfo == null) {
            selectedCourseInfo = CourseInfo.INVALID_COURSE;
        } else {
            selectedCourseInfo = courseInfo;
        }
    }

    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir) {
        final Course course = getCourse(project);
        if (course == null) {
            logger.warn("StepikProjectGenerator: Failed to get builders");
            return;
        }
        StudyTaskManager.getInstance(project).setCourse(course);
        course.setCourseDirectory(new File(CONFIG_COURSES_DIR,
                Integer.toString(selectedCourseInfo.getId())).getAbsolutePath());
    }

    @Nullable
    protected Course getCourse(@NotNull final Project project) {

        final File courseFile = new File(new File(CONFIG_COURSES_DIR, Integer.toString(selectedCourseInfo.getId())),
                EduNames.COURSE_META_FILE);
        if (courseFile.exists()) {
            return readCourseFromCache(courseFile, false);
        }
        return null;
    }

    public List<CourseInfo> getCourses(boolean force) {
        if (CONFIG_COURSES_DIR.exists()) {
            courses = getCoursesFromCache();
        }
        if (force || courses.isEmpty()) {
            List<CourseInfo> tmp = execCancelable(StepikConnectorGet::getEnrolledCourses);
            if (tmp == null) tmp = new ArrayList<>();
            courses = tmp;
            flushCache(courses);
        }
        if (courses.isEmpty()) {
            courses = getBundledIntro();
        }
        return courses;
    }

    @NotNull
    public List<CourseInfo> getCoursesUnderProgress(
            boolean force,
            @NotNull final String progressTitle,
            @NotNull final Project project) {
        try {
            return ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(() -> {
                        ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                        return getCourses(force);
                    }, progressTitle, true, project);
        } catch (RuntimeException e) {
            return Collections.singletonList(CourseInfo.INVALID_COURSE);
        }
    }

    @NotNull
    public SupportedLanguages getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(@NotNull SupportedLanguages defaultLang) {
        this.defaultLang = defaultLang;
    }

    /**
     *    Static methods
     */

    private static List<CourseInfo> getBundledIntro() {
        final File introCourse = new File(CONFIG_COURSES_DIR, "Introduction to Python");
        if (introCourse.exists()) {
            final CourseInfo courseInfo = getCourseInfo(introCourse);

            return Collections.singletonList(courseInfo);
        }
        return Collections.emptyList();
    }

    @Nullable
    private static Course readCourseFromCache(@NotNull File courseFile, boolean isAdaptive) {
        try (BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(new FileInputStream(courseFile), "UTF-8"))){
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(SupportedLanguages.class, new SupportedLanguagesDeserializer())
                    .create();
            final Course course = gson.fromJson(bufferedReader, Course.class);
            course.initCourse(isAdaptive);
            return course;
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    // mock for adaptive course
    @Deprecated
    public static void flushLesson(@NotNull final File lessonDirectory, @NotNull final Lesson lesson) {
        FileUtil.createDirectory(lessonDirectory);
        int taskIndex = 1;
        for (Task task : lesson.getTaskList()) {
            task.setIndex(taskIndex++);
            final File taskDirectory = new File(lessonDirectory, task.getDirectory());
            flushTask(task, taskDirectory);
        }
    }

    // mock for adaptive course
    @Deprecated
    public static void flushTask(@NotNull final Task task, @NotNull final File taskDirectory) {
        FileUtil.createDirectory(taskDirectory);
        for (Map.Entry<String, TaskFile> taskFileEntry : task.taskFiles.entrySet()) {
            final String name = taskFileEntry.getKey();
            final TaskFile taskFile = taskFileEntry.getValue();
            final File file = new File(taskDirectory, name);
            FileUtil.createIfDoesntExist(file);

            try {
                if (EduUtils.isImage(taskFile.getName())) {
                    FileUtil.writeToFile(file, Base64.decodeBase64(taskFile.getText()));
                } else {
                    FileUtil.writeToFile(file, taskFile.getText());
                }
            } catch (IOException e) {
                logger.error("ERROR copying file " + name);
            }
        }
    }

    /**
     * Writes courses to cache file {@link StepikProjectGenerator#CACHE_NAME}
     */
    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
    public static void flushCache(List<CourseInfo> courses) {
        File cacheFile = new File(CONFIG_COURSES_DIR, CACHE_NAME);
        PrintWriter writer = null;
        try {
            if (!createCacheFile(cacheFile)) return;
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

            final Set<CourseInfo> courseInfos = new HashSet<>();
            courseInfos.addAll(courses);
            writer = new PrintWriter(cacheFile);
            for (CourseInfo courseInfo : courseInfos) {
                final String json = gson.toJson(courseInfo);
                writer.println(json);
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            StudyUtils.closeSilently(writer);
        }
    }

    private static boolean createCacheFile(File cacheFile) throws IOException {
        if (!CONFIG_COURSES_DIR.exists()) {
            final boolean created = CONFIG_COURSES_DIR.mkdirs();
            if (!created) {
                logger.error("Cannot flush courses cache. Can't create courses directory");
                return false;
            }
        }
        if (!cacheFile.exists()) {
            final boolean created = cacheFile.createNewFile();
            if (!created) {
                logger.error("Cannot flush courses cache. Can't create " + CACHE_NAME + " file");
                return false;
            }
        }
        return true;
    }

    @NotNull
    public static List<CourseInfo> getCoursesFromCache() {
        List<CourseInfo> courses = new ArrayList<>();
        final File cacheFile = new File(CONFIG_COURSES_DIR, CACHE_NAME);
        if (!cacheFile.exists()) {
            return courses;
        }
        try {
            final FileInputStream inputStream = new FileInputStream(cacheFile);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                .create();
                        final CourseInfo courseInfo = gson.fromJson(line, CourseInfo.class);
                        courses.add(courseInfo);
                    }
                } catch (IOException | JsonSyntaxException e) {
                    logger.error(e.getMessage());
                } finally {
                    StudyUtils.closeSilently(reader);
                }
            } finally {
                StudyUtils.closeSilently(inputStream);
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
        return courses;
    }

    /**
     * Parses course json meta file and finds course name
     *
     * @return information about course or null if course file is invalid
     */
    @Nullable
    private static CourseInfo getCourseInfo(File courseFile) {
        if (courseFile.isDirectory()) {
            File[] courseFiles = courseFile.listFiles((dir, name) -> name.equals(EduNames.COURSE_META_FILE));
            if (courseFiles == null || courseFiles.length != 1) {
                logger.info("More than one or without course files");
                return null;
            }
            courseFile = courseFiles[0];
        }
        CourseInfo courseInfo = null;
        BufferedReader reader = null;
        try {
            if (courseFile.getName().equals(EduNames.COURSE_META_FILE)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(courseFile), "UTF-8"));
                JsonReader r = new JsonReader(reader);
                JsonParser parser = new JsonParser();
                JsonElement el = parser.parse(r);
                String courseName = el.getAsJsonObject().get(COURSE_NAME_ATTRIBUTE).getAsString();
                String courseDescription = el.getAsJsonObject().get(COURSE_DESCRIPTION).getAsString();
                JsonArray courseAuthors = el.getAsJsonObject().get(AUTHOR_ATTRIBUTE).getAsJsonArray();
                String language = el.getAsJsonObject().get(LANGUAGE_ATTRIBUTE).getAsString();
                courseInfo = new CourseInfo();
                courseInfo.setName(courseName);
                courseInfo.setDescription(courseDescription);
                courseInfo.setType("pycharm " + language);
                final ArrayList<StepikUser> authors = new ArrayList<>();
                for (JsonElement author : courseAuthors) {
                    final JsonObject authorAsJsonObject = author.getAsJsonObject();
                    final StepikUser stepikUser = new StepikUser();
                    stepikUser.setFirstName(authorAsJsonObject.get("first_name").getAsString());
                    stepikUser.setLastName(authorAsJsonObject.get("last_name").getAsString());
                    authors.add(stepikUser);
                }
                courseInfo.setAuthors(authors);
            }
        } catch (Exception e) {
            //error will be shown in UI
        } finally {
            StudyUtils.closeSilently(reader);
        }
        return courseInfo;
    }

    public static void downloadAndFlushCourse(Project project, CourseInfo courseInfo) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
            return execCancelable(() -> {
                final Course course = StepikConnectorGet.getCourse(project, courseInfo);
                if (course != null) {
                    flushCourse(project, course);
                    course.initCourse(false);
                }
                return course;
            });
        }, "Downloading Course", true, project);
    }

    private static void flushCourse(@NotNull final Project project, @NotNull final Course course) {
        final File courseDirectory = StudyUtils.getCacheDirectory(project, course);
        FileUtil.createDirectory(courseDirectory);
        flushCourseJson(course, courseDirectory);
    }

    private static void flushCourseJson(@NotNull final Course course, @NotNull final File courseDirectory) {
        final Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(SupportedLanguages.class, new StudySerializationUtils.Json.SupportedLanguagesSerializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        final String json = gson.toJson(course);
        final File courseJson = new File(courseDirectory, EduNames.COURSE_META_FILE);
        try (OutputStreamWriter outputStreamWriter =
                new OutputStreamWriter(new FileOutputStream(courseJson), "UTF-8")) {
            outputStreamWriter.write(json);
        } catch (IOException e) {
            Messages.showErrorDialog(e.getMessage(), "Failed to Generate Json");
            logger.warn(e);
        }
    }
}
