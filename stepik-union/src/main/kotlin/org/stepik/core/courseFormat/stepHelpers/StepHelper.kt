package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.urls.Urls
import org.stepik.core.StepikProjectManager
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.stepHelpers.Actions.NEED_LOGIN
import org.stepik.core.courseFormat.stepHelpers.Actions.NOTHING
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated
import org.stepik.plugin.actions.navigation.StudyNavigator


open class StepHelper(private val project: Project, internal val stepNode: StepNode) : Loggable {

    open fun getStatus(): String = ""

    open fun getAction(): Actions {
        return if (!isAuthenticated) {
            NEED_LOGIN
        } else NOTHING
    }

    val type: String
        get() = stepNode.type.typeName

    val link: String
        get() {
            val stepNode = stepNode
            val parent = stepNode.parent
            var link = Urls.STEPIK_URL
            if (parent != null) {
                link = "$link/lesson/${parent.id}/step/${stepNode.position}"
            }

            return link
        }

    val path: String
        get() = stepNode.path

    val content: String
        get() {
            var content = stepNode.text
            if (!content.startsWith("<p>") && !content.startsWith("<br>")) {
                content = "<p>$content"
            }
            return content
        }

    val isAdaptive: Boolean
        get() = StepikProjectManager.isAdaptive(project)

    val parent: Long
        get() {
            val parent = stepNode.parent ?: return 0

            return parent.id
        }

    open val isAutoCreateAttempt: Boolean
        get() = hasSubmitButton()

    fun solvedLesson(): Boolean {
        try {
            val lesson = stepNode.parent ?: return false
            val data = lesson.data ?: return false
            val progressId = data.progress
            val stepikApiClient = authAndGetStepikApiClient()
            val progresses = stepikApiClient.progresses()
                    .get()
                    .id(progressId!!)
                    .execute()

            return !progresses.isEmpty && progresses.first.isPassed
        } catch (e: StepikClientException) {
            logger.warn(e)
        }

        return false
    }

    fun hasNextStep(): Boolean {
        return StudyNavigator.nextLeaf(stepNode) != null
    }

    open fun hasSubmitButton() = false

    fun needLogin() = getAction() === NEED_LOGIN

    open fun canSubmit() = false
}
