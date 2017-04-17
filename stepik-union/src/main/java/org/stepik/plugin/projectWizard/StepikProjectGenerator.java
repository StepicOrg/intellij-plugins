package org.stepik.plugin.projectWizard;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.objects.courses.Course;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.courseFormat.StudyNodeFactory;
import org.stepik.core.metrics.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED;
import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.metrics.MetricsStatus.TARGET_NOT_FOUND;
import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.getCurrentUser;

public class StepikProjectGenerator {
    public static final StudyObject EMPTY_STUDY_OBJECT = initEmptyStudyNode();
    private static final Logger logger = Logger.getInstance(StepikProjectGenerator.class);
    private static StepikProjectGenerator instance;
    private static StudyNode projectRoot;
    private SupportedLanguages defaultLang = SupportedLanguages.INVALID;

    private StepikProjectGenerator() {
    }

    @NotNull
    private static StudyObject initEmptyStudyNode() {
        StudyObject course = new Course();
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
    public static List<StudyObject> getCourses(@NotNull SupportedLanguages programmingLanguage) {
        List<StudyObject> courses = new ArrayList<>();
        List<Long> coursesIds = getHardcodedCoursesId(programmingLanguage);

        if (!coursesIds.isEmpty()) {
            StepikApiClient stepikApiClient = authAndGetStepikApiClient();
            try {
                courses = stepikApiClient.courses()
                        .get()
                        .id(coursesIds)
                        .execute()
                        .getCourses()
                        .stream()
                        .map(course -> (StudyObject) course)
                        .collect(Collectors.toList());
            } catch (StepikClientException e) {
                logger.warn("Failed get courses", e);
            }
        }

        courses.sort((course1, course2) -> {
            long id1 = course1.getId();
            long id2 = course2.getId();

            int index1 = coursesIds.indexOf(id1);
            int index2 = coursesIds.indexOf(id2);
            return Integer.compare(index1, index2);
        });

        return courses;
    }

    @NotNull
    private static List<Long> getHardcodedCoursesId(@NotNull SupportedLanguages programmingLanguage) {
        switch (programmingLanguage) {
            case JAVA:
            case JAVA8:
                return Arrays.asList(187L, 150L, 217L, 1127L, 125L, 126L);
            case PYTHON3:
                return Arrays.asList(67L, 512L, 401L, 217L, 1127L, 125L, 126L, 150L, 568L, 431L);
            case ASM32:
            case ASM64:
            case CLOJURE:
            case CPP:
            case CPP_11:
            case HASKELL:
            case HASKELL_7_10:
            case JAVASCRIPT:
            case MONO_CS:
            case OCTAVE:
            case R:
            case RUST:
            case SHELL:
                return Arrays.asList(217L, 1127L, 125L, 126L, 150L);
            case C:
            case HASKELL_8_0:
            case RUBY:
            case SCALA:
                return Arrays.asList(1127L, 125L, 126L, 150L);
        }

        return Collections.emptyList();
    }

    public void createCourseNodeUnderProgress(@NotNull final Project project, @NotNull StudyObject data) {
        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(() -> {
                    if (data.getId() == 0) {
                        logger.warn("Failed to get a course");
                        Metrics.createProject(project, DATA_NOT_LOADED);
                        return;
                    }

                    ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
                    indicator.setIndeterminate(true);

                    StepikApiClient stepikApiClient = authAndGetStepikApiClient();
                    projectRoot = StudyNodeFactory.createTree(project, stepikApiClient, data);
                }, "Creating Project", true, project);
    }

    public void generateProject(@NotNull Project project) {
        StepikProjectManager stepikProjectManager = StepikProjectManager.getInstance(project);
        if (stepikProjectManager == null) {
            Metrics.createProject(project, TARGET_NOT_FOUND);
            return;
        }
        stepikProjectManager.setRootNode(projectRoot);
        stepikProjectManager.setDefaultLang(getDefaultLang());
        stepikProjectManager.setCreatedBy(getCurrentUser().getId());
        Metrics.createProject(project, SUCCESSFUL);
    }

    @NotNull
    public SupportedLanguages getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(@NotNull SupportedLanguages defaultLang) {
        this.defaultLang = defaultLang;
    }
}
