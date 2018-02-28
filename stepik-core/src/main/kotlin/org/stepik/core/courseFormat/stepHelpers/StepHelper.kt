package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.steps.Step
import org.stepik.api.urls.Urls
import org.stepik.core.StudyUtils.getConfigurator
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.stepHelpers.Actions.NEED_LOGIN
import org.stepik.core.courseFormat.stepHelpers.Actions.NOTHING


open class StepHelper(val project: Project, internal val stepNode: StepNode) : Loggable {
    val data
        get() = stepNode.data as Step

    private val projectManager = getProjectManager(project)

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

    val parentTitle: String? = stepNode.parent?.data?.title

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

            return progresses.isNotEmpty && progresses.first().isPassed
        } catch (e: StepikClientException) {
            logger.warn(e)
        }

        return false
    }

    fun hasNextStep(): Boolean {
        return getConfigurator(project)?.enabledNextAction(project, stepNode) ?: false
    }

    fun nextButtonCaption(): String {
        return getConfigurator(project)?.nextButtonCaption ?: ""
    }

    open fun hasSubmitButton() = false

    fun needLogin() = getAction() === NEED_LOGIN

    open fun canSubmit() = false
}
