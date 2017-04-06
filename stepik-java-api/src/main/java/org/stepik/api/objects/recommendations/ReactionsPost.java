package org.stepik.api.objects.recommendations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class ReactionsPost {
    private ReactionPost recommendationReaction;

    @NotNull
    public ReactionPost getRecommendationReaction() {
        if (recommendationReaction == null) {
            recommendationReaction = new ReactionPost();
        }
        return recommendationReaction;
    }

    public void setRecommendationReaction(@Nullable ReactionPost recommendationReaction) {
        this.recommendationReaction = recommendationReaction;
    }
}
