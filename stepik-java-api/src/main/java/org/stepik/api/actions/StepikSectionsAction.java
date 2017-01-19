package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.sections.StepikSectionsGetQuery;

/**
 * @author meanmail
 */
public class StepikSectionsAction extends StepikAbstractAction {
    public StepikSectionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikSectionsGetQuery get() {
        return new StepikSectionsGetQuery(this);
    }
}
