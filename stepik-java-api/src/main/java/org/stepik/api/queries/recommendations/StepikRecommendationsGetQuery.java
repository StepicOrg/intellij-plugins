package org.stepik.api.queries.recommendations;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.recommendations.Recommendations;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikRecommendationsGetQuery extends StepikAbstractGetQuery<StepikRecommendationsGetQuery, Recommendations> {
    public StepikRecommendationsGetQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Recommendations.class);
    }

    @NotNull
    public StepikRecommendationsGetQuery course(long course) {
        addParam("course", course);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.RECOMMENDATIONS;
    }

    @Override
    protected boolean isCacheEnabled() {
        return false;
    }
}
