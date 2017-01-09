package org.stepik.api.queries.sections;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikSectionsGetQuery extends StepikAbstractGetQuery<StepikSectionsGetQuery, Sections> {
    public StepikSectionsGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Sections.class);
    }

    @NotNull
    public StepikSectionsGetQuery page(int value) {
        addParam("page", value);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.SECTIONS;
    }
}
