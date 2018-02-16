package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import org.stepik.api.exceptions.StepikClientException
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated


class FreeAnswerQuizHelper(project: Project, stepNode: StepNode) : StringQuizHelper(project, stepNode) {

    private val isFrozen: Boolean
        get() {
            if (!needReview()) {
                return true
            }

            val data = stepNode.data ?: return true

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
                if (!instructions.isEmpty) {
                    val instruction = instructions.first
                    return instruction.isFrozen
                }
            } catch (e: StepikClientException) {
                logger.warn(e)
            }

            return true
        }

    val stageText: String
        get() {
            if (complete()) {
                return "Complete"
            }

            if (needWait()) {
                return "Stage 4 of 4: Wait when other students review for your  submission"
            }

            if (needReview()) {
                return "Stage 3 of 4: You should review submissions of other students"
            }

            if (needSendSubmission()) {
                return "Stage 2 of 4: You should send the solution for review"
            }

            return if (needSolve()) {
                "Stage 1 of 4: You should solve the problem"
            } else ""

        }

    val actionName: String
        get() {
            if (needReview()) {
                return "Start to review"
            }

            return if (needSendSubmission()) {
                "Send the solution for review"
            } else ""
        }

    val isActionEnabled: Boolean
        get() = !needReview() || !isFrozen

    val actionHint: String
        get() = if (needReview() && isFrozen) {
            "Not solutions for review yet"
        } else ""

    init {
        useLastSubmission = true
    }

    fun withReview(): Boolean {
        initStepOptions()
        val data = stepNode.data
        return data != null && data.instruction != 0
    }

    private fun needSolve(): Boolean {
        return needSendSubmission() && "correct" != getStatus()
    }

    private fun needSendSubmission(): Boolean {
        initStepOptions()
        val data = stepNode.data
        return data == null || data.session == 0
    }

    private fun needReview(): Boolean {
        if (needSendSubmission()) {
            return false
        }
        val data = stepNode.data
        return data == null || data.actions.containsKey("do_review")
    }

    private fun needWait(): Boolean {
        // TODO not implemented
        return false
    }

    private fun complete(): Boolean {
        // TODO not implemented
        return false
    }

    fun hasAction(): Boolean {
        return needReview() || needSendSubmission()
    }

    companion object {
        private val logger = Logger.getInstance(FreeAnswerQuizHelper::class.java)
    }
}
