package org.hyperskill.actions.step

import org.hyperskill.api.client.hsAttempts
import org.hyperskill.api.client.hsSubmissions
import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.attempts.Attempt
import org.stepik.api.objects.submissions.Submission
import org.stepik.core.SupportedLanguages
import org.stepik.core.actions.step.CodeQuizSendAction
import org.stepik.core.courseFormat.StepNode

class HSSendAction : CodeQuizSendAction() {
    override fun postAttempt(stepikApiClient: StepikApiClient, stepNode: StepNode): Attempt {
        return stepikApiClient.hsAttempts()
                .post()
                .step(stepNode.id)
                .execute()
    }
    
    override fun postSubmission(stepikApiClient: StepikApiClient, attempt: Long, language: SupportedLanguages,
                                code: String): Submission {
        return stepikApiClient.hsSubmissions()
                .post()
                .attempt(attempt)
                .language(language.langName)
                .code(code)
                .execute()
    }
    
}
