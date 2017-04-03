package org.stepik.api.objects.recommendations;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Recommendations extends ObjectsContainer<Recommendation> {
    private List<Recommendation> recommendations;

    @NotNull
    public List<Recommendation> getRecommendations() {
        if (recommendations == null) {
            recommendations = new ArrayList<>();
        }
        return recommendations;
    }

    @NotNull
    @Override
    public List<Recommendation> getItems() {
        return getRecommendations();
    }

    @NotNull
    @Override
    public Class<Recommendation> getItemClass() {
        return Recommendation.class;
    }
}
