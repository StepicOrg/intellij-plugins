package org.stepik.alt.actions.step

import com.intellij.openapi.project.Project
import org.stepik.api.urls.Urls
import org.stepik.core.actions.step.AbstractOpenInBrowserAction
import org.stepik.core.courseFormat.StudyNode

class OpenInBrowserAction : AbstractOpenInBrowserAction() {
    override fun getLink(project: Project, stepNode: StudyNode): String {
        return Urls.ALT_URL
    }

    override fun getActionId() = ACTION_ID

    companion object {
        private const val ACTION_ID = "Alt.OpenInBrowser"
    }
}
