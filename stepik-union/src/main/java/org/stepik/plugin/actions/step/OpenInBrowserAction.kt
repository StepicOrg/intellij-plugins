package org.stepik.plugin.actions.step

import com.intellij.openapi.project.Project
import org.stepik.api.urls.Urls
import org.stepik.core.StepikProjectManager
import org.stepik.core.actions.step.AbstractOpenInBrowserAction
import org.stepik.core.courseFormat.StepNode

class OpenInBrowserAction : AbstractOpenInBrowserAction() {
    override fun getLink(project: Project, stepNode: StepNode): String {
        val parent = stepNode.parent
        var link = Urls.STEPIK_URL
        if (parent != null) {
            link = "$link/lesson/${parent.id}/step/${stepNode.position}"
            if (StepikProjectManager.isAdaptive(project)) {
                link += "?adaptive=true"
            }
        }
        return link
    }
}
