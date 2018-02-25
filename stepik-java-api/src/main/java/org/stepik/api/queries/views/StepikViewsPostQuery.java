package org.stepik.api.queries.views;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.views.Views;
import org.stepik.api.objects.views.ViewsPost;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikViewsPostQuery extends StepikAbstractPostQuery<Views> {
    private final ViewsPost views = new ViewsPost();

    public StepikViewsPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Views.class);
    }

    @NotNull
    public StepikViewsPostQuery step(long id) {
        views.getView().setStep(id);
        return this;
    }

    @NotNull
    public StepikViewsPostQuery assignment(@Nullable Long assignment) {
        views.getView().setAssignment(assignment);
        return this;
    }

    @NotNull
    @Override
    protected String getBody() {
        return getJsonConverter().toJson(views, false);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.VIEWS;
    }
}
