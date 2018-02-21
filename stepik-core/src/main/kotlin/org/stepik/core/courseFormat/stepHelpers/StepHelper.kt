package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.steps.Step
import org.stepik.api.urls.Urls
import org.stepik.core.ProjectManager
import org.stepik.core.StudyUtils.getConfigurator
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.stepHelpers.Actions.NEED_LOGIN
import org.stepik.core.courseFormat.stepHelpers.Actions.NOTHING
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated


open class StepHelper(val project: Project, internal val stepNode: StepNode) : Loggable {
    val data
        get() = stepNode.data as Step

    private val projectManager = getService(project, ProjectManager::class.java)

    open fun getStatus(): String = ""

    open fun getAction(): Actions {
        return if (!isAuthenticated) NEED_LOGIN else NOTHING
    }

    val type: String
        get() = stepNode.type.typeName

    val link: String
        get() {
            val parent = stepNode.parent ?: return Urls.STEPIK_URL
            return "${Urls.STEPIK_URL}/lesson/${parent.id}/step/${parent.position}"
        }

    val path: String
        get() = stepNode.path

    val content: String
        get() {
            val content = stepNode.text
            if (!content.startsWith("<p>") && !content.startsWith("<br>")) {
                return "<p>$content"
            }
            return content
        }

    val isAdaptive = projectManager?.isAdaptive == true

    val parent: Long = stepNode.parent?.id ?: 0

    open val isAutoCreateAttempt: Boolean
        get() = hasSubmitButton()

    fun solvedLesson(): Boolean {
        try {
            val progressId = stepNode.parent?.data?.progress ?: return false
            val stepikApiClient = authAndGetStepikApiClient()
            val progresses = stepikApiClient.progresses()
                    .get()
                    .id(progressId)
                    .execute()

            return !progresses.isEmpty && progresses.first.isPassed
        } catch (e: StepikClientException) {
            logger.warn(e)
        }

        return false
    }

    fun hasNextStep(): Boolean {
        return getConfigurator(project)?.nextAction(stepNode) != null
    }

    open fun hasSubmitButton() = false

    fun needLogin() = getAction() === NEED_LOGIN

    open fun canSubmit() = false
}
