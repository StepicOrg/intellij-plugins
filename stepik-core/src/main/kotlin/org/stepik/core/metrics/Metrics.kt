package org.stepik.core.metrics

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.metrics.Metric
import org.stepik.core.PluginSettings
import org.stepik.core.SupportedLanguages
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.getProjectManager
import org.stepik.core.metrics.MetricsStatus.SUCCESSFUL
import org.stepik.core.pluginMetricsName
import org.stepik.core.utils.currentProject
import org.stepik.core.utils.version
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object Metrics : Loggable {
    private val session = UUID.randomUUID()
            .toString()
    private val executor = Executors.newScheduledThreadPool(1)
    private val pluginId = getService(PluginSettings::class.java).pluginId
    
    private fun postMetrics(
            project: Project,
            metric: Metric,
            status: MetricsStatus) {
        executor.schedule({
            val stepikApiClient: StepikApiClient = authAndGetStepikApiClient()
            if (!isAuthenticated) {
                return@schedule
            }
            
            val appInfo = ApplicationInfo.getInstance()
            
            val query = stepikApiClient.metrics()
                    .post()
                    .timestamp(System.currentTimeMillis() / 1000L)
                    .tags(metric.tags!!)
                    .data(metric.data!!)
                    .name("ide_plugin")
                    .tags("name", pluginMetricsName)
                    .tags("ide_name", appInfo.versionName)
                    .data("ide_version", appInfo.build.toString())
                    .data("plugin_version", version(pluginId))
                    .data("session", session)
                    .tags("status", status)
            
            val projectManager = getProjectManager(project)
            
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
        val metric = Metric().addTags("action", action)
        postMetrics(project, metric, status)
    }
    
    fun authenticate(status: MetricsStatus = SUCCESSFUL) {
        postSimpleMetric(currentProject, "authenticate", status)
    }
    
    fun createProject(project: Project, status: MetricsStatus = SUCCESSFUL) {
        postSimpleMetric(project, "create_project", status)
    }
    
    fun openProject(project: Project, status: MetricsStatus = SUCCESSFUL) {
        postSimpleMetric(project, "open_project", status)
    }
    
    private fun stepAction(
            actionName: String,
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus) {
        val metric = Metric().apply {
            addTags("action", actionName)
            addData("step_id", stepNode.id)
            addTags("step_programming_language", stepNode.currentLang.langName)
            addTags("step_type", stepNode.type.typeName)
        }
        postMetrics(project, metric, status)
    }
    
    fun sendAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus = SUCCESSFUL) {
        stepAction("send", project, stepNode, status)
    }
    
    fun downloadAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus = SUCCESSFUL) {
        stepAction("download", project, stepNode, status)
    }
    
    fun getStepStatusAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus = SUCCESSFUL) {
        stepAction("get_step_status", project, stepNode, status)
    }
    
    fun insertAmbientCodeAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus = SUCCESSFUL) {
        stepAction("insert_ambient_code", project, stepNode, status)
    }
    
    fun navigateAction(
            project: Project,
            studyNode: StudyNode,
            status: MetricsStatus = SUCCESSFUL) {
        if (studyNode is StepNode) {
            stepAction("navigate", project, studyNode, status)
        }
    }
    
    fun openInBrowserAction(
            project: Project,
            studyNode: StudyNode) {
        if (studyNode is StepNode) {
            stepAction("open_in_browser", project, studyNode, SUCCESSFUL)
        }
    }
    
    fun resetStepAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus = SUCCESSFUL) {
        stepAction("reset_step", project, stepNode, status)
    }
    
    fun removeAmbientCodeAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus = SUCCESSFUL) {
        stepAction("remove_ambient_code", project, stepNode, status)
    }
    
    fun switchLanguage(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus = SUCCESSFUL) {
        stepAction("switch_language", project, stepNode, status)
    }
    
    fun testCodeAction(
            project: Project,
            stepNode: StepNode,
            status: MetricsStatus = SUCCESSFUL) {
        stepAction("test_code", project, stepNode, status)
    }
}
