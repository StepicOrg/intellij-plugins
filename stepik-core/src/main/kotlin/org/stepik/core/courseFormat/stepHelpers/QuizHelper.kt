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
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.currentUser
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyStatus
import org.stepik.core.courseFormat.stepHelpers.Actions.GET_ATTEMPT
import org.stepik.core.courseFormat.stepHelpers.Actions.GET_FIRST_ATTEMPT
import org.stepik.core.courseFormat.stepHelpers.Actions.NEED_LOGIN
import org.stepik.core.courseFormat.stepHelpers.Actions.SUBMIT
import org.stepik.core.host

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
            return submission?.replyUrl ?: ""
        }
    
    val baseUrl: String
        get() = host
    
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
            return submission?.hint ?: ""
        }
    
    val isHasSubmissionsRestrictions: Boolean
        get() = data.isHasSubmissionsRestrictions
    
    private val maxSubmissionsCount: Int
        get() = data.maxSubmissionsCount
    
    override val isAutoCreateAttempt: Boolean
        get() = !isHasSubmissionsRestrictions && hasSubmitButton()
    
    private fun loadAttempt(stepikApiClient: StepikApiClient, userId: Long): Boolean {
        val attempts = stepikApiClient.attempts()
                .get<Attempts>()
                .step(stepNode.id)
                .user(userId)
                .execute()
        
        attempt = attempts.firstOrDefault(Attempt())
        
        if (attempts.isEmpty) {
            action = GET_FIRST_ATTEMPT
            return false
        }
        
        action = if (attempt.status == ACTIVE) SUBMIT else GET_ATTEMPT
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
        
        if (submissions.isNotEmpty) {
            val lastSubmission = submissions.first()
            submission = lastSubmission
            
            val isLastSubmission = lastSubmission.id == stepNode.lastSubmissionId
            val outdated = stepNode.lastReplyTime.after(lastSubmission.time)
            if (isLastSubmission && outdated) {
                reply = stepNode.lastReply
                isModified = lastSubmission.reply != reply
            } else {
                reply = lastSubmission.reply
                stepNode.lastReply = reply
                stepNode.lastSubmissionId = lastSubmission.id
            }
            if (attemptId == lastSubmission.attempt.toLong()) {
                status = lastSubmission.status
            }
            if (attempt.status == ACTIVE && status == "correct") {
                action = GET_ATTEMPT
            }
            
            stepNode.status = StudyStatus.of(status)
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
    
    override fun canSubmit() = true
    
    companion object {
        private const val ACTIVE = "active"
        private const val UNCHECKED = "unchecked"
    }
}
