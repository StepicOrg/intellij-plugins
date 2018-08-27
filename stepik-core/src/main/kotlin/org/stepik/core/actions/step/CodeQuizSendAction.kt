package org.stepik.core.actions.step

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.attempts.Attempt
import org.stepik.api.objects.submissions.Submission
import org.stepik.core.SupportedLanguages
import org.stepik.core.actions.SendAction.checkStepStatus
import org.stepik.core.actions.getShortcutText
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED
import org.stepik.core.metrics.MetricsStatus.FAILED_POST
import org.stepik.core.pluginId
import org.stepik.core.pluginName
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow
import org.stepik.core.testFramework.toolWindow.showTestResultsToolWindow
import org.stepik.core.utils.getFileText
import org.stepik.core.utils.getOrCreateSrcDirectory
import org.stepik.core.utils.getTextUnderDirectives

abstract class CodeQuizSendAction : CodeQuizAction(TEXT, DESCRIPTION, AllStepikIcons.ToolWindow.checkTask) {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        FileDocumentManager.getInstance()
                .saveAllDocuments()
        
        logger.info("Start checking step")
        
        val stepNode = getCurrentCodeStepNode(project)
        
        if (stepNode == null) {
            logger.info("Stop checking step: step is null or it is not StepNode ")
            return
        }
        
        val title = "${stepNode.parent?.name ?: "Lesson"} : ${stepNode.name}"
        
        val resultWindow = showTestResultsToolWindow(project, title).apply {
            clear()
            println("Test method: send to $pluginName")
        }
        
        getApplication().executeOnPooledThread {
            val stepikApiClient = authAndGetStepikApiClient(true)
            if (!isAuthenticated) {
                return@executeOnPooledThread
            }
            
            val submissionId = sendStep(stepikApiClient, project, stepNode, resultWindow)
                               ?: return@executeOnPooledThread
            
            Metrics.sendAction(project, stepNode)
            
            checkStepStatus(project, stepikApiClient, stepNode, submissionId, resultWindow)
            logger.info("Finish checking step: id=${stepNode.id}")
        }
    }
    
    override fun getShortcuts() = arrayOf(SHORTCUT)
    
    override fun getActionId() = ACTION_ID
    
    abstract fun postAttempt(stepikApiClient: StepikApiClient, stepNode: StepNode): Attempt?
    
    abstract fun postSubmission(stepikApiClient: StepikApiClient, attempt: Long, language: SupportedLanguages,
                                code: String): Submission?
    
    private fun getAttemptId(
            stepikApiClient: StepikApiClient,
            stepNode: StepNode,
            resultWindow: StepikTestResultToolWindow): Long? {
        val attempt: Attempt?
        try {
            attempt = postAttempt(stepikApiClient, stepNode)
            
            if (attempt == null) {
                resultWindow.println("Error: can't get attempt")
                return null
            }
        } catch (e: StepikClientException) {
            resultWindow.apply {
                println("Error: can't get attempt")
                println(e.message ?: "Unknown error")
            }
            return null
        }
        
        return attempt.id
    }
    
    private fun getSubmissionId(
            project: Project,
            stepikApiClient: StepikApiClient,
            stepNode: StepNode,
            intAttemptId: Long,
            resultWindow: StepikTestResultToolWindow): Long? {
        val currentLang = stepNode.currentLang
        
        val code = getCode(project, stepNode, currentLang)
        if (code == null) {
            logger.info("Sending step failed: id=${stepNode.id}. Step content is null")
            return null
        }
        
        val submission: Submission?
        try {
            submission = postSubmission(stepikApiClient, intAttemptId, currentLang, code)
            
            if (submission == null) {
                resultWindow.println("Error: can't send the submission")
                return null
            }
        } catch (e: StepikClientException) {
            resultWindow.apply {
                println("Error: can't send the submission")
                println(e.message ?: "Unknown error")
            }
            return null
        }
        
        return submission.id
    }
    
    private fun sendStep(
            stepikApiClient: StepikApiClient,
            project: Project,
            stepNode: StepNode,
            resultWindow: StepikTestResultToolWindow): Long? {
        val stepId = stepNode.id
        
        logger.info("Start sending step: id=$stepId")
        
        val intAttemptId = getAttemptId(stepikApiClient, stepNode, resultWindow)
        if (intAttemptId == null) {
            Metrics.sendAction(project, stepNode, DATA_NOT_LOADED)
            return null
        }
        
        val submissionId = getSubmissionId(project, stepikApiClient, stepNode, intAttemptId, resultWindow)
        if (submissionId == null) {
            Metrics.sendAction(project, stepNode, FAILED_POST)
            return null
        }
        
        logger.info("Finish sending step: id=$stepId")
        
        return submissionId
    }
    
    companion object : Loggable {
        private val ACTION_ID = "$pluginId.CodeQuizSendAction"
        private const val SHORTCUT = "ctrl alt pressed ENTER"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Submit solution ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Submit solution for the current step"
        
        private fun getCode(
                project: Project,
                stepNode: StepNode,
                currentLang: SupportedLanguages): String? {
            val src = getOrCreateSrcDirectory(project, stepNode, true) ?: return null
            val mainFile = src.findChild(currentLang.mainFileName) ?: return null
            val text = getFileText(mainFile)
            return getTextUnderDirectives(text, currentLang)
        }
        
    }
}
