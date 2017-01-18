package org.stepik.plugin.projectWizard;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.courses.Courses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jetbrains.tmp.learning.StudyUtils.execCancelable;

public class StepikProjectGenerator {
    public static final Course EMPTY_COURSE = initEmptyCourse();
    private static final Logger logger = Logger.getInstance(StepikProjectGenerator.class);
    private static StepikProjectGenerator instance;
    private SupportedLanguages defaultLang = SupportedLanguages.INVALID;
    private Course selectedCourse = EMPTY_COURSE;

    private StepikProjectGenerator() {
    }

    @NotNull
    private static Course initEmptyCourse() {
        Course course = new Course();
        course.setTitle("Empty");
        course.setDescription("Please, press refresh button");
        return course;
    }

    @NotNull
    public static StepikProjectGenerator getInstance() {
        if (instance == null) {
            instance = new StepikProjectGenerator();
        }
        return instance;
    }

    @NotNull
    private static List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        List<Long> coursesIds = getHardcodedCoursesId();

        if (!coursesIds.isEmpty()) {
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
            try {
                courses = stepikApiClient.courses()
                        .get()
                        .id(coursesIds)
                        .execute()
                        .getCourses();
            } catch (StepikClientException e) {
                logger.warn("Failed get courses", e);
            }
        }

        return courses;
    }

    @NotNull
    private static List<Long> getHardcodedCoursesId() {
        return Arrays.asList(187L, 67L, 512L, 401L, 217L, 150L, 125L, 126L, 1127L);
    }

    @NotNull
    public static List<Course> getCoursesUnderProgress(@NotNull final Project project) {
        try {
            return ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(() -> {
                        ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                        List<Course> courses = getCourses();
                        if (courses.isEmpty()) {
                            courses.add(EMPTY_COURSE);
                        }
                        return courses;
                    }, "Refreshing Course List", true, project);
        } catch (RuntimeException e) {
            return Collections.singletonList(EMPTY_COURSE);
        }
    }

    private static Course getCourseUnderProgress(@Nullable Project project, long id) {
        return ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
            return execCancelable(() -> {
                StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
                Courses courses;
                try {
                    courses = stepikApiClient.courses()
                            .get()
                            .id(id)
                            .execute();
                } catch (StepikClientException e) {
                    return null;
                }
                if (courses.getCount() == 0) {
                    return null;
                }

                return courses.getCourses().get(0);
            });
        }, "Downloading Course", true, project);
    }

    public void setSelectedCourse(@Nullable Course course) {
        if (course == null) {
            selectedCourse = StepikProjectGenerator.EMPTY_COURSE;
        } else {
            selectedCourse = course;
        }
    }

    public void generateProject(@NotNull Project project) {
        final Course course = getCourseUnderProgress(project, selectedCourse.getId());
        if (course == null) {
            logger.warn("Failed to get a course: id = " + selectedCourse.getId());
            return;
        }
        StepikProjectManager stepikProjectManager = StepikProjectManager.getInstance(project);
        stepikProjectManager.setCourseNode(new CourseNode(course));
    }

    @NotNull
    public SupportedLanguages getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(@NotNull SupportedLanguages defaultLang) {
        this.defaultLang = defaultLang;
    }
}
