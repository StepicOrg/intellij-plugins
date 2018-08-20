package org.hyperskill

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import org.stepik.core.ProjectGenerator
import org.stepik.core.StudyBasePluginConfigurator
import org.stepik.core.StudyPluginConfigurator
import org.stepik.core.actions.step.DownloadSubmission
import org.stepik.core.actions.step.StepikResetStepAction
import org.stepik.core.actions.step.StepikSendAction
import org.stepik.core.actions.step.TestSamplesAction
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.isStepikProject
import org.stepik.core.projectWizard.idea.BaseModuleBuilder
import org.hyperskill.actions.navigation.OpenProblemAction
import org.hyperskill.actions.step.OpenInBrowserAction
import org.hyperskill.projectWizard.StepikProjectGenerator
import org.hyperskill.projectWizard.idea.SandboxModuleBuilder
import org.hyperskill.projectWizard.idea.StepModuleBuilder

class HyperskillProjectConfigurator : StudyBasePluginConfigurator() {
    
    override val pluginDescription = "Welcome to the Hyperskill! " +
                                     "Using our adaptive system you can learn some of basic Java topics. " +
                                     "We will at first test your knowledge and then give you lessons, " +
                                     "which will help you to learn new topics and to apply knowledge in " +
                                     "solving practice."
    
    override val nextButtonCaption = OpenProblemAction.DESCRIPTION
    
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
        return OpenProblemAction.loadProblem(project, node)
    }
    
    override fun enabledNextAction(project: Project, currentNode: StepNode?): Boolean {
        return true
    }
    
    override fun getActionGroup(project: Project): DefaultActionGroup {
        val group = DefaultActionGroup()
        group.addAll(
                StepikSendAction(),
                TestSamplesAction(),
                OpenProblemAction(),
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
                "org.hyperskill.studyPluginConfigurator")
        
    }
}
