package org.stepik.plugin.actions.step

import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.attempts.Attempt
import org.stepik.api.objects.attempts.Attempts
import org.stepik.api.objects.submissions.Submission
import org.stepik.core.SupportedLanguages
import org.stepik.core.actions.step.CodeQuizSendAction
import org.stepik.core.courseFormat.StepNode

class StepikSendAction : CodeQuizSendAction() {
    override fun postAttempt(stepikApiClient: StepikApiClient, stepNode: StepNode): Attempt? {
        val attempts = stepikApiClient.attempts()
                .post<Attempts>()
                .step(stepNode.id)
                .execute()
        
        if (attempts.isEmpty) {
            return null
        }
        
        return attempts.first()
    }
    
    override fun postSubmission(stepikApiClient: StepikApiClient, attempt: Long, language: SupportedLanguages,
                                code: String): Submission? {
        val submissions = stepikApiClient.submissions()
                .post()
                .attempt(attempt)
                .language(language.langName)
                .code(code)
                .execute()
        
        if (submissions.isEmpty) {
            return null
        }
        
        return submissions.first()
    }
    
}
