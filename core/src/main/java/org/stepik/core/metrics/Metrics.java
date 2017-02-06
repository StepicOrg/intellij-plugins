package org.stepik.core.metrics;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.queries.metrics.StepikMetricsPostQuery;
import org.stepik.core.utils.PluginUtils;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author meanmail
 */
public class Metrics {
    private static final Logger logger = Logger.getInstance(Metrics.class);
    private static final String session = UUID.randomUUID().toString();

    public static void downloadAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("download", project, stepNode, status);
    }

    private static void postMetrics(@NotNull Project project, @NotNull Consumer<StepikMetricsPostQuery> installer) {
        StepikMetricsPostQuery query = null;
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

            query = stepikApiClient.metrics()
                    .post()
                    .name("ide_plugin")
                    .tags("name", "S_Union")
                    .tags("ide_name", ApplicationInfo.getInstance().getVersionName())
                    .data("ide_version", ApplicationInfo.getInstance().getBuild().toString())
                    .data("plugin_version", PluginUtils.getVersion())
                    .data("session", session);

            StepikProjectManager projectManager = StepikProjectManager.getInstance(project);

            if (projectManager != null) {
                query.data("project_id", projectManager.getUuid());
            }

            installer.accept(query);

            query.execute();
        } catch (StepikClientException e) {
            String message = String.format("Failed post metric: %s", query != null ? query.toString() : "null");
            logger.warn(message, e);
        }
    }

    public static void sendAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("send", project, stepNode, status);
    }

    public static void getStepStatusAction(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        stepAction("get_step_status", project, stepNode, status);
    }

    private static void stepAction(
            @NotNull String actionName,
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull MetricsStatus status) {
        CourseNode courseNode = stepNode.getCourse();
        long courseId = courseNode != null ? courseNode.getId() : 0;

        Consumer<StepikMetricsPostQuery> installer = query -> query.tags("action", actionName)
                .data("course_id", courseId)
                .data("step_id", stepNode.getId())
                .tags("status", status)
                .tags("programming_language", stepNode.getCurrentLang().getName())
                .tags("step_type", stepNode.getData().getBlock().getName());

        postMetrics(project, installer);
    }
}
