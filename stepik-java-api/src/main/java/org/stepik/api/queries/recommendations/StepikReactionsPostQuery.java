package org.stepik.api.queries.recommendations;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.recommendations.ReactionValues;
import org.stepik.api.objects.recommendations.Reactions;
import org.stepik.api.objects.recommendations.ReactionsPost;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikReactionsPostQuery extends StepikAbstractPostQuery<Reactions> {
    private final ReactionsPost reactions = new ReactionsPost();

    public StepikReactionsPostQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Reactions.class);
    }

    @NotNull
    public StepikReactionsPostQuery user(long user) {
        reactions.getRecommendationReaction().setUser(user);
        return this;
    }

    @NotNull
    public StepikReactionsPostQuery lesson(long lesson) {
        reactions.getRecommendationReaction().setLesson(lesson);
        return this;
    }

    @NotNull
    public StepikReactionsPostQuery reaction(@NotNull ReactionValues reaction) {
        reactions.getRecommendationReaction().setReaction(reaction.getValue());
        return this;
    }

    @NotNull
    @Override
    protected String getBody() {
        return getJsonConverter().toJson(reactions);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.RECOMMENDATION_REACTIONS;
    }
}
