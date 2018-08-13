package org.stepik.api.queries.recommendations

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.recommendations.ReactionValues
import org.stepik.api.objects.recommendations.Reactions
import org.stepik.api.objects.recommendations.ReactionsPost
import org.stepik.api.queries.StepikAbstractPostQuery

class StepikReactionsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<Reactions>(stepikAction, Reactions::class.java) {
    
    private val reactions = ReactionsPost()
    
    fun user(user: Long): StepikReactionsPostQuery {
        reactions.recommendationReaction.user = user
        return this
    }
    
    fun lesson(lesson: Long): StepikReactionsPostQuery {
        reactions.recommendationReaction.lesson = lesson
        return this
    }
    
    fun reaction(reaction: ReactionValues): StepikReactionsPostQuery {
        reactions.recommendationReaction.reaction = reaction.value
        return this
    }
    
    override fun getBody(): String {
        return jsonConverter.toJson(reactions, false)
    }
    
    override fun getUrl(): String {
        return "${stepikAction.stepikApiClient.host}/api/recommendation-reactions"
    }
}
