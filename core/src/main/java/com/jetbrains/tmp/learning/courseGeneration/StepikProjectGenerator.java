package com.jetbrains.tmp.learning.courseGeneration;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.JsonConverter;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.courses.Courses;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.jetbrains.tmp.learning.StudyUtils.execCancelable;

public class StepikProjectGenerator {
    public static final Course EMPTY_COURSE = initEmptyCourse();
    private static final Path CACHE_PATH = Paths.get(PathManager.getConfigPath(), "stepik-union", "cache");
    private static final Logger logger = Logger.getInstance(StepikProjectGenerator.class);
    private static StepikProjectGenerator instance;
    @NotNull
    private SupportedLanguages defaultLang = SupportedLanguages.INVALID;
    @NotNull
    private Course selectedCourseInfo = EMPTY_COURSE;
    private StepikProjectGenerator() {
    }

    private static Course initEmptyCourse() {
        Course course = new Course();
        course.setTitle("Empty");
        course.setDescription("Please, press refresh button");
        return course;
    }

    public static StepikProjectGenerator getInstance() {
        if (instance == null) {
            instance = new StepikProjectGenerator();
        }
        return instance;
    }

    @NotNull
    private static List<Course> getCourses(boolean force) {
        List<Course> courses = new ArrayList<>();

        List<Integer> coursesIds = getHardcodedCoursesId();

        if (!force) {
            courses = getCoursesFromCache();
        }

        courses.forEach(course -> coursesIds.remove(course.getId()));

        if (!coursesIds.isEmpty()) {
            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();
            List<Course> additional = stepikApiClient.courses()
                    .get()
                    .id(coursesIds)
                    .execute()
                    .getCourses();
            courses.addAll(additional);
            flushCourses(additional);
        }

        if (courses.isEmpty()) {
            courses.add(EMPTY_COURSE);
        }
        return courses;
    }

    @NotNull
    private static List<Integer> getHardcodedCoursesId() {
        return Arrays.asList(187, 67, 512, 401, 217, 150, 125, 126, 1127);
    }

    @NotNull
    public static List<Course> getCoursesUnderProgress(
            boolean force,
            @NotNull final Project project) {
        try {
            return ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(() -> {
                        ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                        List<Course> courses = getCourses(force);
                        if (courses.isEmpty()) {
                            courses.add(EMPTY_COURSE);
                        }
                        flushCourses(courses);
                        return courses;
                    }, "Refreshing Course List", true, project);
        } catch (RuntimeException e) {
            return Collections.singletonList(EMPTY_COURSE);
        }
    }

    private static void flushCourses(List<Course> courses) {
        courses.forEach(StepikProjectGenerator::flushCourse);
    }

    @NotNull
    private static List<Course> getCoursesFromCache() {
        List<Course> courses = new ArrayList<>();
        Path cacheCourses = CACHE_PATH.resolve("courses");

        if (!Files.exists(cacheCourses)) {
            return courses;
        }

        JsonConverter jsonConverter = StepikConnectorLogin.getStepikApiClient()
                .getJsonConverter();

        try {
            Files.walkFileTree(cacheCourses, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Optional<String> content = Files.readAllLines(file)
                            .stream()
                            .reduce((line, text) -> text + line);

                    if (content.isPresent()) {
                        Course course = jsonConverter.fromJson(content.get(), Course.class);
                        courses.add(course);
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            return courses;
        }

        return courses;
    }

    public static void downloadAndFlushCourse(@Nullable Project project, int id) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
            return execCancelable(() -> {
                StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();
                Courses courses = stepikApiClient.courses()
                        .get()
                        .id(id)
                        .execute();
                if (courses.getCount() == 0) {
                    return null;
                }

                flushCourse(courses.getCourses().get(0));
                return null;
            });
        }, "Downloading Course", true, project);
    }

    private static void flushCourse(Course course) {
        Path courseCache = CACHE_PATH.resolve("courses").resolve(course.getId() + ".json");

        try {
            Files.createDirectories(courseCache.getParent());
            JsonConverter jsonConverter = StepikConnectorLogin.getStepikApiClient()
                    .getJsonConverter();
            Files.write(courseCache, jsonConverter.toJson(course).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    public void setSelectedCourse(@Nullable Course course) {
        if (course == null) {
            selectedCourseInfo = StepikProjectGenerator.EMPTY_COURSE;
        } else {
            selectedCourseInfo = course;
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
        StepikProjectManager stepikProjectManager = StepikProjectManager.getInstance(project);
        stepikProjectManager.setCourse(new com.jetbrains.tmp.learning.courseFormat.Course(course));
    }

    @Nullable
    private Course getCourse() {
        Path courseCache = CACHE_PATH.resolve("courses").resolve(selectedCourseInfo.getId() + ".json");

        if (!Files.exists(courseCache)) {
            return null;
        }

        JsonConverter jsonConverter = StepikConnectorLogin.getStepikApiClient()
                .getJsonConverter();

        Optional<String> content;
        try {
            content = Files.readAllLines(courseCache)
                    .stream()
                    .reduce((line, text) -> text + line);
        } catch (IOException e) {
            return null;
        }

        return content.map(s -> jsonConverter.fromJson(s, Course.class)).orElse(null);
    }

    @NotNull
    public SupportedLanguages getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(@NotNull SupportedLanguages defaultLang) {
        this.defaultLang = defaultLang;
    }
}
