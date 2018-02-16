package org.stepik.plugin

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import org.stepik.core.StepikProjectManager
import org.stepik.core.StudyBasePluginConfigurator
import org.stepik.core.actions.step.DownloadSubmission
import org.stepik.core.actions.step.InsertStepikDirectives
import org.stepik.core.actions.step.StepikResetStepAction
import org.stepik.core.actions.step.StepikSendAction
import org.stepik.core.actions.step.TestSamplesAction
import org.stepik.plugin.actions.navigation.StepikNextStepAction
import org.stepik.plugin.actions.navigation.StepikPreviousStepAction
import org.stepik.plugin.actions.step.OpenInBrowserAction

class StepikPluginConfigurator : StudyBasePluginConfigurator() {
    override fun getActionGroup(project: Project): DefaultActionGroup {
        val group = DefaultActionGroup()

        group.add(StepikSendAction())
        group.add(TestSamplesAction())
        group.add(StepikPreviousStepAction())
        group.add(StepikNextStepAction())
        group.add(StepikResetStepAction())
        group.add(DownloadSubmission())
        group.add(InsertStepikDirectives())
        group.add(OpenInBrowserAction())

        return group
    }

    override fun accept(project: Project): Boolean {
        return StepikProjectManager.isStepikProject(project)
    }
}
