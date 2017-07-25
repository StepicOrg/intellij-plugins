package org.stepik.plugin.actions

import com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.submissions.Submission
import org.stepik.core.StepikProjectManager
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyStatus
import org.stepik.core.courseFormat.StudyStatus.SOLVED
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.SUCCESSFUL
import org.stepik.core.metrics.MetricsStatus.TIME_OVER
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow

object SendAction {
    private val logger = Logger.getInstance(SendAction::class.java)
    private val EVALUATION = "evaluation"
    private val PERIOD = 2 * 1000L //ms
    private val FIVE_MINUTES = 5 * ActionUtils.MILLISECONDS_IN_MINUTES //ms

    fun checkStepStatus(
            project: Project,
            stepikApiClient: StepikApiClient,
            stepNode: StepNode,
            submissionId: Long,
            resultWindow: StepikTestResultToolWindow) {
        val stepIdString = "id=" + stepNode.id
        logger.info("Started check a status for step: " + stepIdString)
        var stepStatus = EVALUATION
        var timer = 0L
        var showedTimer = false

        var currentSubmission: Submission? = null
        while (timer < FIVE_MINUTES) {
            try {
                val submission = stepikApiClient.submissions()
                        .get()
                        .id(submissionId)
                        .execute()

                if (!submission.isEmpty) {
                    currentSubmission = submission.first
                    if (showedTimer) {
                        resultWindow.clearLastLine()
                    } else {
                        showedTimer = true
                    }
                    ActionUtils.setupCheckProgress(resultWindow, currentSubmission!!, timer)
                    stepStatus = currentSubmission.status
                    if (EVALUATION != stepStatus) {
                        if (showedTimer) {
                            resultWindow.clearLastLine()
                        }
                        break
                    }
                }

                Thread.sleep(PERIOD)
                timer += PERIOD
            } catch (e: Exception) {
                if (showedTimer) {
                    resultWindow.clearLastLine()
                }
                resultWindow.println("Error: can't get submission status", ERROR_OUTPUT)
                resultWindow.println(e.message ?: "Unknown error", ERROR_OUTPUT)
                logger.info("Stop check a status for step: " + stepIdString, e)
                return
            }
        }

        if (currentSubmission == null) {
            logger.info("Stop check a status for step: $stepIdString without result")
            return
        }
        val actionStatus = if (EVALUATION == stepStatus) TIME_OVER else SUCCESSFUL
        Metrics.getStepStatusAction(project, stepNode, actionStatus)

        if (StudyStatus.of(stepStatus) == SOLVED) {
            stepNode.passed()
        }
        resultWindow.println(stepStatus)
        resultWindow.println(currentSubmission.hint)

        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                ProjectView.getInstance(project).refresh()
            }
            StepikProjectManager.updateSelection(project)
        }
        logger.info("Finish check a status for step: $stepIdString with status: $stepStatus")
    }
}
