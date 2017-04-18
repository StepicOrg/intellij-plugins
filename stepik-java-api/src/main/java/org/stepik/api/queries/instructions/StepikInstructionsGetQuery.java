package org.stepik.api.queries.instructions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.instructions.Instructions;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikInstructionsGetQuery extends StepikAbstractGetQuery<StepikInstructionsGetQuery, Instructions> {
    public StepikInstructionsGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Instructions.class);
    }

    @NotNull
    public StepikInstructionsGetQuery page(int page) {
        addParam("page", page);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.INSTRUCTIONS;
    }
}
