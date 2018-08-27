package org.hyperskill.api.queries.submissions

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.submissions.Attachment
import org.stepik.api.objects.submissions.Reply
import org.stepik.api.objects.submissions.Submission
import org.stepik.api.objects.submissions.SubmissionsPost
import org.stepik.api.queries.StepikAbstractPostQuery

class HSSubmissionsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<Submission>(stepikAction, Submission::class.java) {
    
    private val submissions = SubmissionsPost()
    
    override val url = "${stepikAction.stepikApiClient.host}/api/submissions/"
    
    fun attempt(id: Long): HSSubmissionsPostQuery {
        submissions.submission.attempt = id
        return this
    }
    
    fun text(text: String?): HSSubmissionsPostQuery {
        submissions.submission.reply.setText(text)
        return this
    }
    
    fun attachments(attachments: List<Attachment>?): HSSubmissionsPostQuery {
        submissions.submission.reply.setAttachments(attachments)
        return this
    }
    
    fun formula(formula: String?): HSSubmissionsPostQuery {
        submissions.submission.reply.setFormula(formula)
        return this
    }
    
    fun number(number: String?): HSSubmissionsPostQuery {
        submissions.submission.reply.setNumber(number)
        return this
    }
    
    fun file(file: String?): HSSubmissionsPostQuery {
        submissions.submission.reply.setFile(file)
        return this
    }
    
    fun choices(choices: List<*>?): HSSubmissionsPostQuery {
        submissions.submission.reply.setChoices(choices)
        return this
    }
    
    fun blanks(blanks: List<String>?): HSSubmissionsPostQuery {
        submissions.submission.reply.setBlanks(blanks)
        return this
    }
    
    fun ordering(ordering: List<Int>): HSSubmissionsPostQuery {
        submissions.submission.reply.setOrdering(ordering)
        return this
    }
    
    fun language(value: String): HSSubmissionsPostQuery {
        submissions.submission.reply.setLanguage(value)
        return this
    }
    
    fun code(value: String): HSSubmissionsPostQuery {
        submissions.submission.reply.setCode(value)
        return this
    }
    
    override val body: String
        get() {
            return jsonConverter.toJson(submissions.submission, false)
        }
    
    fun reply(reply: Reply): HSSubmissionsPostQuery {
        submissions.submission.setReply(reply)
        return this
    }
}
