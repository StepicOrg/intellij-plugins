package org.stepik.core.actions.step

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.attempts.Attempts
import org.stepik.api.objects.submissions.Submissions
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
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow
import org.stepik.core.testFramework.toolWindow.StepikTestToolWindowUtils.Companion.showTestResultsToolWindow
import org.stepik.core.utils.getFileText
import org.stepik.core.utils.getOrCreateSrcDirectory
import org.stepik.core.utils.getTextUnderDirectives

class StepikSendAction : CodeQuizAction(TEXT, DESCRIPTION, AllStepikIcons.ToolWindow.checkTask) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        FileDocumentManager.getInstance().saveAllDocuments()

        logger.info("Start checking step")

        val stepNode = getCurrentCodeStepNode(project)
        if (stepNode == null) {
            logger.info("Stop checking step: step is null or it is not StepNode ")
            return
        }
        val title = "${stepNode.parent?.name ?: "Lesson"} : ${stepNode.name}"
        val resultWindow = showTestResultsToolWindow(project, title)
        resultWindow.apply {
            clear()
            println("Test method: send to Stepik")
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

    companion object : Loggable {
        private const val ACTION_ID = "STEPIC.StepikSendAction"
        private const val SHORTCUT = "ctrl alt pressed ENTER"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Check Step ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Check current step"

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

        private fun getAttemptId(
                stepikApiClient: StepikApiClient,
                stepNode: StepNode,
                resultWindow: StepikTestResultToolWindow): Long? {
            val attempts: Attempts
            try {
                attempts = stepikApiClient.attempts()
                        .post<Attempts>()
                        .step(stepNode.id)
                        .execute()
                if (attempts.isEmpty) {
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

            return attempts.first().id
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

            val submissions: Submissions
            try {
                submissions = stepikApiClient.submissions()
                        .post()
                        .attempt(intAttemptId)
                        .language(currentLang.langName)
                        .code(code)
                        .execute()
                if (submissions.isEmpty) {
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

            return submissions.first().id
        }

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
