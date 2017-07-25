package org.stepik.core.metrics;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.metrics.Metric;
import org.stepik.api.queries.metrics.StepikMetricsPostQuery;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StepType;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.utils.PluginUtils;
import org.stepik.core.utils.Utils;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.isAuthenticated;

/**
 * @author meanmail
 */
public class Metrics {
    private static final Logger logger = Logger.getInstance(Metrics.class);
    private static final String session = UUID.randomUUID().toString();
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static void postMetrics(
            @NotNull Project project,
            @NotNull Metric metric,
            @NotNull MetricsStatus status) {
        executor.schedule(() -> {
            StepikApiClient stepikApiClient;
            stepikApiClient = authAndGetStepikApiClient();
            if (!isAuthenticated()) {
                return;
            }

            final StepikMetricsPostQuery query = stepikApiClient.metrics()
                    .post()
                    .timestamp(System.currentTimeMillis() / 1000L)
                    .tags(metric.getTags())
                    .data(metric.getData())
                    .name("ide_plugin")
                    .tags("name", "S_Union")
                    .tags("ide_name", ApplicationInfo.getInstance().getVersionName())
                    .data("ide_version", ApplicationInfo.getInstance().getBuild().toString())
                    .data("plugin_version", PluginUtils.getVersion())
                    .data("session", session)
                    .tags("status", status);

            StepikProjectManager projectManager = StepikProjectManager.getInstance(project);

            if (projectManager != null) {
                query.data("project_id", projectManager.getUuid())
                        .tags("project_programming_language", projectManager.getDefaultLang().getLangName())
                        .data("project_manager_version", projectManager.getVersion());

                StudyNode projectRoot = projectManager.getProjectRoot();

                if (projectRoot != null) {
                    Class<? extends StudyNode> projectRootClass = projectRoot.getClass();
                    query.tags("project_root_class", projectRootClass.getSimpleName())
                            .data("project_root_id", projectRoot.getId());

                    query.data("course_id", projectRoot.getCourseId(stepikApiClient));
                }
            }

            query.executeAsync()
                    .exceptionally((e) -> {
                        String message = String.format("Failed post metric: %s", query.toString());
                        logger.warn(message, e);
                        return null;
                    });
        }, 500, TimeUnit.MILLISECONDS);
    }

    private static void postSimpleMetric(
            @NotNull Project project,
            @NotNull String action,
            @NotNull MetricsStatus status) {
        Metric metric = new Metric();
        metric.addTags("action", action);

        postMetrics(project, metric, status);
    }

    public static void authenticate(@NotNull MetricsStatus status) {
        Project project = Utils.getCurrentProject();
        postSimpleMetric(project, "authenticate", status);
    }

    public static void createProject(@NotNull Project project, @NotNull MetricsStatus status) {
        postSimpleMetric(project, "create_project", status);
    }

    public static void openProject(@NotNull Project project, @NotNull MetricsStatus status) {
        postSimpleMetric(project, "open_project", status);
    }

    private static void stepAction(
            @NotNull String actionName,
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        Metric metric = new Metric();
        metric.addTags("action", actionName);
        metric.addData("step_id", stepNode.getId());
        metric.addTags("step_programming_language", stepNode.getCurrentLang().getLangName());
        StepType stepType = stepNode.getType();
        metric.addTags("step_type", stepType.getName());

        postMetrics(project, metric, status);
    }

    public static void sendAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("send", project, stepNode, status);
    }

    public static void downloadAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("download", project, stepNode, status);
    }

    public static void getStepStatusAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("get_step_status", project, stepNode, status);
    }

    public static void insertAmbientCodeAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("insert_ambient_code", project, stepNode, status);
    }

    public static void navigateAction(
            @NotNull Project project,
            @NotNull StudyNode studyNode,
            @NotNull MetricsStatus status) {
        if (studyNode instanceof StepNode) {
            StepNode stepNode = (StepNode) studyNode;
            stepAction("navigate", project, stepNode, status);
        }
    }

    public static void openInBrowserAction(
            @NotNull Project project,
            @NotNull StudyNode studyNode) {
        if (studyNode instanceof StepNode) {
            StepNode stepNode = (StepNode) studyNode;
            stepAction("open_in_browser", project, stepNode, SUCCESSFUL);
        }
    }

    public static void resetStepAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("reset_step", project, stepNode, status);
    }

    public static void removeAmbientCodeAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("remove_ambient_code", project, stepNode, status);
    }

    public static void switchLanguage(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("switch_language", project, stepNode, status);
    }
}
