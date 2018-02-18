package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.attempts.Attempt
import org.stepik.api.objects.attempts.Attempts
import org.stepik.api.objects.attempts.Component
import org.stepik.api.objects.attempts.Dataset
import org.stepik.api.objects.submissions.Reply
import org.stepik.api.objects.submissions.Submission
import org.stepik.api.objects.submissions.Submissions
import org.stepik.api.queries.Order
import org.stepik.api.urls.Urls
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyStatus
import org.stepik.core.courseFormat.stepHelpers.Actions.GET_ATTEMPT
import org.stepik.core.courseFormat.stepHelpers.Actions.GET_FIRST_ATTEMPT
import org.stepik.core.courseFormat.stepHelpers.Actions.NEED_LOGIN
import org.stepik.core.courseFormat.stepHelpers.Actions.SUBMIT
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.currentUser


open class QuizHelper(project: Project, stepNode: StepNode) : StepHelper(project, stepNode) {
    internal var reply = Reply()
    internal var useLastSubmission: Boolean = false
    private var action = GET_FIRST_ATTEMPT
    private var status = "unchecked"
    private var attempt = Attempt()
    private var submissionsCount = -1
    private var submission: Submission? = null
    private var initialized: Boolean = false
    var isModified: Boolean = false
        private set

    val attemptId: Long
        get() {
            initStepOptions()
            return attempt.id
        }

    internal val dataset: Dataset
        get() {
            initStepOptions()
            return attempt.dataset
        }

    val datasetUrl: String
        get() {
            initStepOptions()
            return attempt.datasetUrl
        }

    val timeLeft: Int
        get() {
            initStepOptions()
            return attempt.timeLeft
        }

    val replyUrl: String
        get() {
            initStepOptions()
            return if (submission != null) submission!!.replyUrl else ""
        }

    val baseUrl: String
        get() = Urls.STEPIK_URL

    val components: List<Component>
        get() = dataset.components

    val blanks: List<String>
        get() {
            initStepOptions()
            return reply.blanks
        }

    val formula: String
        get() {
            initStepOptions()
            return reply.formula
        }

    val hint: String
        get() {
            initStepOptions()
            return submission!!.hint
        }

    val isHasSubmissionsRestrictions: Boolean
        get() {
            val data = stepNode.data
            return data != null && data.isHasSubmissionsRestrictions
        }

    private val maxSubmissionsCount: Int
        get() {
            val data = stepNode.data ?: return 0
            return data.maxSubmissionsCount
        }

    override val isAutoCreateAttempt: Boolean
        get() = !isHasSubmissionsRestrictions && hasSubmitButton()

    private fun loadAttempt(stepikApiClient: StepikApiClient, userId: Long): Boolean {
        val attempts = stepikApiClient.attempts()
                .get<Attempts>()
                .step(stepNode.id)
                .user(userId)
                .execute()
        if (attempts.isEmpty) {
            attempt = Attempt()
            action = GET_FIRST_ATTEMPT
            return false
        }

        attempt = attempts.first
        action = if (ACTIVE == attempt.status) SUBMIT else GET_ATTEMPT
        return true
    }

    private fun loadSubmission(stepikApiClient: StepikApiClient, userId: Long) {
        val attemptId = attempt.id

        val query = stepikApiClient.submissions()
                .get()
                .order(Order.DESC)
                .user(userId)
                .step(stepNode.id)

        if (!useLastSubmission) {
            query.attempt(attemptId)
        }

        val submissions = query.execute()
        isModified = false

        if (!submissions.isEmpty) {
            submission = submissions.first

            val lastSubmission = submission!!.id == stepNode.lastSubmissionId
            val outdated = stepNode.lastReplyTime.after(submission!!.time)
            if (lastSubmission && outdated) {
                reply = stepNode.lastReply
                isModified = submission!!.reply != reply
            } else {
                reply = submission!!.reply
                stepNode.setLastReply(submission!!.reply)
                stepNode.lastSubmissionId = submission!!.id
            }
            if (attemptId == submission!!.attempt.toLong()) {
                status = submission!!.status
            }
            if (ACTIVE == attempt.status && status == "correct") {
                action = GET_ATTEMPT
            }

            stepNode.setStatus(StudyStatus.of(status))
        } else {
            reply = stepNode.lastReply
            isModified = true
        }
    }

    override fun getStatus(): String {
        initStepOptions()
        return status
    }

    internal fun initStepOptions() {
        if (initialized) {
            return
        }

        initialized = true
        status = UNCHECKED
        action = GET_FIRST_ATTEMPT

        val stepikApiClient = authAndGetStepikApiClient()
        val user = currentUser
        if (user.isGuest) {
            action = NEED_LOGIN
            fail()
            initialized = false
            return
        }

        val userId = user.id

        try {
            if (!loadAttempt(stepikApiClient, userId)) {
                fail()
                initialized = false
                return
            }

            loadSubmission(stepikApiClient, userId)

            done()
            initialized = true
        } catch (e: StepikClientException) {
            logger.warn("Failed init test-step options", e)
            fail()
        }

    }

    internal open fun done() {}

    internal open fun fail() {}

    private fun getSubmissionsCount(): Int {
        if (submissionsCount == -1) {
            val stepikApiClient = authAndGetStepikApiClient()
            val user = currentUser
            if (user.isGuest) {
                action = NEED_LOGIN
                return 0
            }
            val userId = user.id
            submissionsCount = 0
            var page = 1

            var submissions: Submissions
            do {
                try {
                    submissions = stepikApiClient.submissions()
                            .get()
                            .step(stepNode.id)
                            .user(userId)
                            .page(page)
                            .execute()
                } catch (e: StepikClientException) {
                    logger.warn("Failed get submissions count", e)
                    return 0
                }

                submissionsCount += submissions.count
                page++
            } while (submissions.meta.hasNext)
        }
        return submissionsCount
    }

    override fun getAction(): Actions {
        initStepOptions()
        return action
    }

    fun submissionsLeft(): Int {
        return maxSubmissionsCount - getSubmissionsCount()
    }

    override fun hasSubmitButton(): Boolean {
        val locked = isHasSubmissionsRestrictions && getSubmissionsCount() >= maxSubmissionsCount
        return !needLogin() && !locked
    }

    override fun canSubmit(): Boolean {
        return true
    }

    companion object {
        private const val ACTIVE = "active"
        private const val UNCHECKED = "unchecked"
    }
}
