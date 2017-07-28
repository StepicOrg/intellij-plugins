package org.stepik.plugin.actions.step

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import icons.AllStepikIcons
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.attempts.Attempts
import org.stepik.api.objects.submissions.Submissions
import org.stepik.core.SupportedLanguages
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED
import org.stepik.core.metrics.MetricsStatus.FAILED_POST
import org.stepik.core.metrics.MetricsStatus.SUCCESSFUL
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow
import org.stepik.core.testFramework.toolWindow.StepikTestToolWindowUtils
import org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory
import org.stepik.core.utils.getFileText
import org.stepik.core.utils.getTextUnderDirectives
import org.stepik.plugin.actions.ActionUtils
import org.stepik.plugin.actions.SendAction

class StepikSendAction : CodeQuizAction(TEXT, DESCRIPTION, AllStepikIcons.ToolWindow.checkTask) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        FileDocumentManager.getInstance().saveAllDocuments()

        logger.info("Start checking step")

        val stepNode = CodeQuizAction.getCurrentCodeStepNode(project)
        if (stepNode == null) {
            logger.info("Stop checking step: step is null or it is not StepNode ")
            return
        }
        val title = "${stepNode.parent?.name ?: "Lesson"} : ${stepNode.name}"
        val resultWindow = StepikTestToolWindowUtils.showTestResultsToolWindow(project, title)
        resultWindow.clear()
        resultWindow.println("Test method: send to Stepik")

        ApplicationManager.getApplication()
                .executeOnPooledThread {
                    val stepikApiClient = authAndGetStepikApiClient(true)
                    if (!isAuthenticated()) {
                        return@executeOnPooledThread
                    }

                    val submissionId = sendStep(stepikApiClient, project, stepNode, resultWindow)
                            ?: return@executeOnPooledThread

                    Metrics.sendAction(project, stepNode, SUCCESSFUL)

                    SendAction.checkStepStatus(project, stepikApiClient, stepNode, submissionId, resultWindow)
                    logger.info("Finish checking step: id=${stepNode.id}")
                }
    }

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun getActionId() = ACTION_ID

    companion object {
        private val logger = Logger.getInstance(StepikSendAction::class.java)
        private val ACTION_ID = "STEPIC.StepikSendAction"
        private val SHORTCUT = "ctrl alt pressed ENTER"
        private val SHORTCUT_TEXT = ActionUtils.getShortcutText(SHORTCUT)
        private val TEXT = "Check Step ($SHORTCUT_TEXT)"
        private val DESCRIPTION = "Check current step"

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
                resultWindow.println("Error: can't get attempt")
                resultWindow.println(e.message ?: "Unknown error")
                return null
            }

            return attempts.first.id
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
                resultWindow.println("Error: can't send the submission")
                resultWindow.println(e.message ?: "Unknown error")
                return null
            }

            return submissions.first.id
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
