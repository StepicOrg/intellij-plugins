package org.stepik.core.metrics

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.metrics.Metric
import org.stepik.core.StepikProjectManager
import org.stepik.core.SupportedLanguages
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.metrics.MetricsStatus.SUCCESSFUL
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated
import org.stepik.core.utils.PluginUtils
import org.stepik.core.utils.Utils
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


object Metrics {
    private val logger = Logger.getInstance(Metrics::class.java)
    private val session = UUID.randomUUID().toString()
    private val executor = Executors.newScheduledThreadPool(1)

    private fun postMetrics(
            project: Project,
            metric: Metric,
            status: MetricsStatus) {
        executor.schedule({
            val stepikApiClient: StepikApiClient = authAndGetStepikApiClient()
            if (!isAuthenticated) {
                return@schedule
            }

            val query = stepikApiClient.metrics()
                    .post()
                    .timestamp(System.currentTimeMillis() / 1000L)
                    .tags(metric.tags)
                    .data(metric.data)
                    .name("ide_plugin")
                    .tags("name", "S_Union")
                    .tags("ide_name", ApplicationInfo.getInstance().versionName)
                    .data("ide_version", ApplicationInfo.getInstance().build.toString())
                    .data("plugin_version", PluginUtils.version)
                    .data("session", session)
                    .tags("status", status)

            val projectManager = StepikProjectManager.getInstance(project)

            if (projectManager != null) {
                query.data("project_id", projectManager.getUuid())
                        .tags("project_programming_language",
                                (projectManager.defaultLang ?: SupportedLanguages.INVALID).langName)
                        .data("project_manager_version", projectManager.version)

                val projectRoot = projectManager.projectRoot

                if (projectRoot != null) {
                    val projectRootClass = projectRoot.javaClass
                    query.tags("project_root_class", projectRootClass.simpleName)
                            .data("project_root_id", projectRoot.id)

                    query.data("course_id", projectRoot.getCourseId(stepikApiClient))
                }
            }

            query.executeAsync()
                    .exceptionally { e ->
                        val message = String.format("Failed post metric: %s", query.toString())
                        logger.warn(message, e)
                        null
                    }
        }, 500, TimeUnit.MILLISECONDS)
    }

    private fun postSimpleMetric(
            project: Project,
            action: String,
            status: MetricsStatus) {
        val metric = Metric()
        metric.addTags("action", action)

        postMetrics(project, metric, status)
    }

    fun authenticate(status: MetricsStatus) {
        val project = Utils.currentProject
        postSimpleMetric(project, "authenticate", status)
    }

    fun createProject(project: Project, status: MetricsStatus) {
        postSimpleMetric(project, "create_project", status)
    }

    fun openProject(project: Project, status: MetricsStatus) {
        postSimpleMetric(project, "open_project", status)
    }

    private fun stepAction(
            actionName: String,
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        val metric = Metric()
        metric.addTags("action", actionName)
        metric.addData("step_id", stepNode.id)
        metric.addTags("step_programming_language", stepNode.currentLang.langName)
        val stepType = stepNode.type
        metric.addTags("step_type", stepType.name)

        postMetrics(project, metric, status)
    }

    fun sendAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        stepAction("send", project, stepNode, status)
    }

    fun downloadAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        stepAction("download", project, stepNode, status)
    }

    fun getStepStatusAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        stepAction("get_step_status", project, stepNode, status)
    }

    fun insertAmbientCodeAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        stepAction("insert_ambient_code", project, stepNode, status)
    }

    fun navigateAction(
            project: Project,
            studyNode: StudyNode<*, *>,
            status: MetricsStatus) {
        if (studyNode is StepNode) {
            stepAction("navigate", project, studyNode, status)
        }
    }

    fun openInBrowserAction(
            project: Project,
            studyNode: StudyNode<*, *>) {
        if (studyNode is StepNode) {
            stepAction("open_in_browser", project, studyNode, SUCCESSFUL)
        }
    }

    fun resetStepAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        stepAction("reset_step", project, stepNode, status)
    }

    fun removeAmbientCodeAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        stepAction("remove_ambient_code", project, stepNode, status)
    }

    fun switchLanguage(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        stepAction("switch_language", project, stepNode, status)
    }

    fun testCodeAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        stepAction("test_code", project, stepNode, status)
    }
}
