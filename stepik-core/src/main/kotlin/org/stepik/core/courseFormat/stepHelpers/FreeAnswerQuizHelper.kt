package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.api.exceptions.StepikClientException
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated


class FreeAnswerQuizHelper(project: Project, stepNode: StepNode) : StringQuizHelper(project, stepNode) {

    init {
        useLastSubmission = true
    }

    private val isFrozen: Boolean
        get() {
            if (!needReview()) {
                return true
            }

            val instructionId = data.instruction
            if (instructionId == 0) {
                return true
            }

            val stepikClient = authAndGetStepikApiClient()
            if (!isAuthenticated) {
                return true
            }

            try {
                val instructions = stepikClient.instructions()
                        .get()
                        .id(instructionId)
                        .execute()
                if (instructions.isNotEmpty) {
                    return instructions.first().isFrozen
                }
            } catch (e: StepikClientException) {
                logger.warn(e)
            }

            return true
        }

    val stageText: String
        get() {
            return when {
                complete() -> "Complete"
                needWait() -> "Stage 4 of 4: Wait when other students review for your  submission"
                needReview() -> "Stage 3 of 4: You should review submissions of other students"
                needSendSubmission() -> "Stage 2 of 4: You should send the solution for review"
                needSolve() -> "Stage 1 of 4: You should solve the problem"
                else -> ""
            }
        }

    val actionName: String
        get() {
            return when {
                needReview() -> "Start to review"
                needSendSubmission() -> "Send the solution for review"
                else -> ""
            }
        }

    val isActionEnabled: Boolean
        get() = !needReview() || !isFrozen

    val actionHint: String
        get() = if (needReview() && isFrozen) "Not solutions for review yet" else ""

    fun withReview(): Boolean {
        initStepOptions()
        return data.instruction != 0
    }

    private fun needSolve(): Boolean {
        return needSendSubmission() && getStatus() != "correct"
    }

    private fun needSendSubmission(): Boolean {
        initStepOptions()
        return data.session.isEmpty()
    }

    private fun needReview(): Boolean {
        if (needSendSubmission()) {
            return false
        }
        return data.actions.containsKey("do_review")
    }

    private fun needWait(): Boolean {
        // TODO not implemented
        return false
    }

    private fun complete(): Boolean {
        // TODO not implemented
        return false
    }

    fun hasAction() = needReview() || needSendSubmission()
}
