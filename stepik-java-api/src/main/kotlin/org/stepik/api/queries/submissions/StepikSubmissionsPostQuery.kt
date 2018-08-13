package org.stepik.api.queries.submissions

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.submissions.Attachment
import org.stepik.api.objects.submissions.Reply
import org.stepik.api.objects.submissions.Submissions
import org.stepik.api.objects.submissions.SubmissionsPost
import org.stepik.api.queries.StepikAbstractPostQuery

class StepikSubmissionsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<Submissions>(stepikAction, Submissions::class.java) {
    
    private val submissions = SubmissionsPost()
    
    override fun getUrl(): String {
        return "${stepikAction.stepikApiClient.host}/api/submissions"
    }
    
    fun attempt(id: Long): StepikSubmissionsPostQuery {
        submissions.submission.attempt = id
        return this
    }
    
    fun text(text: String?): StepikSubmissionsPostQuery {
        submissions.submission.reply.setText(text)
        return this
    }
    
    fun attachments(attachments: List<Attachment>?): StepikSubmissionsPostQuery {
        submissions.submission.reply.setAttachments(attachments)
        return this
    }
    
    fun formula(formula: String?): StepikSubmissionsPostQuery {
        submissions.submission.reply.setFormula(formula)
        return this
    }
    
    fun number(number: String?): StepikSubmissionsPostQuery {
        submissions.submission.reply.setNumber(number)
        return this
    }
    
    fun file(file: String?): StepikSubmissionsPostQuery {
        submissions.submission.reply.setFile(file)
        return this
    }
    
    fun choices(choices: List<*>?): StepikSubmissionsPostQuery {
        submissions.submission.reply.setChoices(choices)
        return this
    }
    
    fun blanks(blanks: List<String>?): StepikSubmissionsPostQuery {
        submissions.submission.reply.setBlanks(blanks)
        return this
    }
    
    fun ordering(ordering: List<Int>): StepikSubmissionsPostQuery {
        submissions.submission.reply.setOrdering(ordering)
        return this
    }
    
    fun language(value: String): StepikSubmissionsPostQuery {
        submissions.submission.reply.setLanguage(value)
        return this
    }
    
    fun code(value: String): StepikSubmissionsPostQuery {
        submissions.submission.reply.setCode(value)
        return this
    }
    
    override fun getBody(): String {
        return jsonConverter.toJson(submissions, false)
    }
    
    fun reply(reply: Reply): StepikSubmissionsPostQuery {
        submissions.submission.setReply(reply)
        return this
    }
}
