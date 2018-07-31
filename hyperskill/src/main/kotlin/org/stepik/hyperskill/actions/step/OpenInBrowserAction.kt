package org.stepik.hyperskill.actions.step

import com.intellij.openapi.project.Project
import org.stepik.api.urls.Urls
import org.stepik.core.actions.step.AbstractOpenInBrowserAction
import org.stepik.core.courseFormat.StudyNode

class OpenInBrowserAction : AbstractOpenInBrowserAction() {
    
    override fun getLink(project: Project, stepNode: StudyNode): String {
        val lessonId = stepNode.parent?.id ?: return Urls.HYPERSKILL_URL
        return "${Urls.HYPERSKILL_URL}/learn/lesson/$lessonId"
    }
    
}
