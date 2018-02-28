package org.stepik.alt

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import org.stepik.alt.actions.navigation.LoadProblemAction
import org.stepik.alt.actions.step.OpenInBrowserAction
import org.stepik.alt.projectWizard.StepikProjectGenerator
import org.stepik.alt.projectWizard.idea.SandboxModuleBuilder
import org.stepik.alt.projectWizard.idea.StepModuleBuilder
import org.stepik.core.ProjectGenerator
import org.stepik.core.StudyBasePluginConfigurator
import org.stepik.core.StudyPluginConfigurator
import org.stepik.core.StudyUtils.isStepikProject
import org.stepik.core.actions.step.DownloadSubmission
import org.stepik.core.actions.step.StepikResetStepAction
import org.stepik.core.actions.step.StepikSendAction
import org.stepik.core.actions.step.TestSamplesAction
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.projectWizard.idea.BaseModuleBuilder

class AltProjectConfigurator : StudyBasePluginConfigurator() {

    override val pluginDescription = "Welcome to the Stepik ALT platform! " +
            "Using our adaptive system you can learn some of basic Java topics. " +
            "We will at first test your knowledge and then give you lessons, " +
            "which will help you to learn new topics and to apply knowledge in solving practice."

    override val nextButtonCaption = LoadProblemAction.DESCRIPTION

    override fun getSandboxModuleBuilder(path: String): BaseModuleBuilder? {
        return SandboxModuleBuilder(path)
    }

    override fun getStepModuleBuilder(moduleDir: String, step: StepNode): BaseModuleBuilder? {
        return StepModuleBuilder(moduleDir, step)
    }

    override fun getProjectGenerator(): ProjectGenerator? {
        return StepikProjectGenerator
    }

    override fun nextAction(project: Project, node: StepNode?): StudyNode? {
        return LoadProblemAction.loadProblem(project, node)
    }

    override fun enabledNextAction(project: Project, currentNode: StepNode?): Boolean {
        return true
    }

    override fun getActionGroup(project: Project): DefaultActionGroup {
        val group = DefaultActionGroup()
        group.addAll(
                StepikSendAction(),
                TestSamplesAction(),
                LoadProblemAction(),
                StepikResetStepAction(),
                DownloadSubmission(),
                OpenInBrowserAction()
        )

        return group
    }

    override fun accept(project: Project): Boolean {
        return isStepikProject(project)
    }


    companion object {
        val EP_NAME = ExtensionPointName.create<StudyPluginConfigurator>(
                "org.stepik.alt.studyPluginConfigurator")

    }
}
