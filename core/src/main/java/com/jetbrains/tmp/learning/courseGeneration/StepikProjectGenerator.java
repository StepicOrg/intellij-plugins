package com.jetbrains.tmp.learning.courseGeneration;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudySerializationUtils;
import com.jetbrains.tmp.learning.StudySerializationUtils.Json.SupportedLanguagesDeserializer;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.stepik.CourseInfo;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jetbrains.tmp.learning.StudyUtils.execCancelable;

public class StepikProjectGenerator {
    private static final File CONFIG_COURSES_DIR = new File(PathManager.getConfigPath(), "courses");

    private static final Logger logger = Logger.getInstance(StepikProjectGenerator.class);
    private static final String CACHE_NAME = "enrolledCourseNames.txt";
    private static StepikProjectGenerator instance;
    @NotNull
    private SupportedLanguages defaultLang = SupportedLanguages.INVALID;
    @NotNull
    private CourseInfo selectedCourseInfo = CourseInfo.INVALID_COURSE;

    private StepikProjectGenerator() {}

    public static StepikProjectGenerator getInstance() {
        if (instance == null) {
            instance = new StepikProjectGenerator();
        }
        return instance;
    }

    @NotNull
    private static List<CourseInfo> getCourses(boolean force) {
        List<CourseInfo> courses = new ArrayList<>();
        if (CONFIG_COURSES_DIR.exists()) {
            courses = getCoursesFromCache();
        }
        if (force || courses.isEmpty()) {
            courses = StepikConnectorGet.getCourses(getHardcodedCoursesId());
            flushCache(courses);
        }
        if (courses.isEmpty()) {
            courses.add(CourseInfo.INVALID_COURSE);
        }
        return courses;
    }

    @NotNull
    private static List<Integer> getHardcodedCoursesId() {
        return Arrays.asList(187, 67, 512, 401, 217, 150, 125, 126, 1127);
    }

    @NotNull
    public static List<CourseInfo> getCoursesUnderProgress(
            boolean force,
            @NotNull final Project project) {
        try {
            return ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(() -> {
                        ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                        List<CourseInfo> courses = getCourses(force);
                        if (courses.isEmpty()) courses.add(CourseInfo.INVALID_COURSE);
                        flushCache(courses);
                        return courses;
                    }, "Refreshing Course List", true, project);
        } catch (RuntimeException e) {
            return Collections.singletonList(CourseInfo.INVALID_COURSE);
        }
    }

    @Nullable
    private static Course readCourseFromCache(
            @NotNull File courseFile,
            @SuppressWarnings("SameParameterValue") boolean isAdaptive) {
        try (BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(new FileInputStream(courseFile), "UTF-8"))) {
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

    /**
     * Writes courses to cache file {@link StepikProjectGenerator#CACHE_NAME}
     */
    private static void flushCache(List<CourseInfo> courses) {
        File cacheFile = new File(CONFIG_COURSES_DIR, CACHE_NAME);
        try {
            if (!createCacheFile(cacheFile)) {
                return;
            }
        } catch (IOException e) {
            logger.error(e);
        }

        try (PrintWriter writer = new PrintWriter(cacheFile)) {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

            final Set<CourseInfo> courseInfos = new HashSet<>();
            courseInfos.addAll(courses);

            for (CourseInfo courseInfo : courseInfos) {
                final String json = gson.toJson(courseInfo);
                writer.println(json);
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private static boolean createCacheFile(@NotNull File cacheFile) throws IOException {
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
    private static List<CourseInfo> getCoursesFromCache() {
        List<CourseInfo> courses = new ArrayList<>();
        final File cacheFile = new File(CONFIG_COURSES_DIR, CACHE_NAME);
        if (!cacheFile.exists()) {
            return courses;
        }
        try (FileInputStream inputStream = new FileInputStream(cacheFile)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                    Gson gson = gsonBuilder.create();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final CourseInfo courseInfo = gson.fromJson(line, CourseInfo.class);
                        courses.add(courseInfo);
                    }
                } catch (IOException | JsonSyntaxException e) {
                    logger.error(e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return courses;
    }

    public static void downloadAndFlushCourse(
            @Nullable Project project,
            @NotNull CourseInfo courseInfo) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
            return execCancelable(() -> {
                final Course course = StepikConnectorGet.getCourse(courseInfo);
                if (course != null) {
                    flushCourse(course);
                }
                return null;
            });
        }, "Downloading Course", true, project);
    }

    private static void flushCourse(@NotNull final Course course) {
        final File cacheDirectory = new File(course.getCacheDirectory());
        FileUtil.createDirectory(cacheDirectory);
        flushCourseJson(course, cacheDirectory);
    }

    private static void flushCourseJson(
            @NotNull final Course course,
            @NotNull final File courseDirectory) {
        final Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(SupportedLanguages.class,
                        new StudySerializationUtils.Json.SupportedLanguagesSerializer())
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

    public void setSelectedCourse(@Nullable CourseInfo courseInfo) {
        if (courseInfo == null) {
            selectedCourseInfo = CourseInfo.INVALID_COURSE;
        } else {
            selectedCourseInfo = courseInfo;
        }
    }

    /**
     * get course from cache and set it in StepikProjectManager
     */
    public void generateProject(@NotNull Project project) {
        final Course course = getCourse();
        if (course == null) {
            logger.warn("StepikProjectGenerator: Failed to get builders");
            return;
        }
        StepikProjectManager.getInstance(project).setCourse(course);
    }

    @Nullable
    private Course getCourse() {
        File cacheDirectory = new File(CONFIG_COURSES_DIR, Integer.toString(selectedCourseInfo.getId()));
        final File courseFile = new File(cacheDirectory, EduNames.COURSE_META_FILE);
        if (courseFile.exists()) {
            return readCourseFromCache(courseFile, false);
        }
        return null;
    }

    @NotNull
    public SupportedLanguages getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(@NotNull SupportedLanguages defaultLang) {
        this.defaultLang = defaultLang;
    }
}
