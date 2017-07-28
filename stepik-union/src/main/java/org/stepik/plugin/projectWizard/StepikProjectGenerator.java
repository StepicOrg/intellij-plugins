package org.stepik.plugin.projectWizard;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectEx;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED;
import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.metrics.MetricsStatus.TARGET_NOT_FOUND;
import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.getCurrentUser;
import static org.stepik.plugin.projectWizard.CoursesList.COURSES;

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
    public static CompletableFuture<List<StudyObject>> getCourses(@NotNull SupportedLanguages programmingLanguage) {
        return CompletableFuture.supplyAsync(() -> {
            List<Long> coursesIds = COURSES.getOrDefault(programmingLanguage, emptyList());

            if (coursesIds.isEmpty()) {
                return emptyList();
            }

            StepikApiClient stepikApiClient = authAndGetStepikApiClient();
            try {
                return stepikApiClient.courses()
                        .get()
                        .id(coursesIds)
                        .execute()
                        .getCourses()
                        .stream()
                        .map(course -> (StudyObject) course)
                        .sorted((course1, course2) -> {
                            long id1 = course1.getId();
                            long id2 = course2.getId();

                            int index1 = coursesIds.indexOf(id1);
                            int index2 = coursesIds.indexOf(id2);
                            return Integer.compare(index1, index2);
                        })
                        .collect(Collectors.toList());
            } catch (StepikClientException e) {
                logger.warn("Failed get courses", e);
                return emptyList();
            }
        });
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

        ((ProjectEx) project).setProjectName(projectRoot.getName());

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
