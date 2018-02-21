package org.stepik.alt

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import org.stepik.alt.actions.navigation.StepikNextStepAction
import org.stepik.alt.actions.step.OpenInBrowserAction
import org.stepik.core.ProjectGenerator
import org.stepik.core.StudyBasePluginConfigurator
import org.stepik.core.StudyUtils.isStepikProject
import org.stepik.core.actions.step.DownloadSubmission
import org.stepik.core.actions.step.InsertStepikDirectives
import org.stepik.core.actions.step.StepikResetStepAction
import org.stepik.core.actions.step.StepikSendAction
import org.stepik.core.actions.step.TestSamplesAction
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.projectWizard.idea.BaseModuleBuilder

class StepikPluginConfigurator : StudyBasePluginConfigurator() {
    override fun getSandboxModuleBuilder(path: String): BaseModuleBuilder? {
        return null
    }

    override fun getStepModuleBuilder(moduleDir: String, step: StepNode): BaseModuleBuilder? {
        return null
    }

    override fun getProjectGenerator(): ProjectGenerator? {
        return null
    }

    override fun nextAction(node: StepNode): StudyNode? {
        return StepikNextStepAction.getNextStep()
    }

    override fun getActionGroup(project: Project): DefaultActionGroup {
        val group = DefaultActionGroup()
        group.addAll(
                StepikSendAction(),
                TestSamplesAction(),
                StepikNextStepAction(),
                StepikResetStepAction(),
                DownloadSubmission(),
                InsertStepikDirectives(),
                OpenInBrowserAction()
        )

        return group
    }

    override fun accept(project: Project): Boolean {
        return isStepikProject(project)
    }
}
