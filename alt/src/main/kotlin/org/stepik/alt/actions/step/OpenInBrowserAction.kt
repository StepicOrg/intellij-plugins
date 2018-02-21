package org.stepik.alt.actions.step

import com.intellij.openapi.project.Project
import org.stepik.api.urls.Urls
import org.stepik.core.actions.step.AbstractOpenInBrowserAction
import org.stepik.core.courseFormat.StudyNode

class OpenInBrowserAction : AbstractOpenInBrowserAction() {
    override fun getLink(project: Project, stepNode: StudyNode): String {
        val lessonId = stepNode.parent?.id ?: return Urls.ALT_URL
        return "${Urls.ALT_URL}/topics/lesson/$lessonId"
    }

    override fun getActionId() = ACTION_ID

    companion object {
        private const val ACTION_ID = "Alt.OpenInBrowser"
    }
}
