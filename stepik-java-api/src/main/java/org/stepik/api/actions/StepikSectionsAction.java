package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.sections.StepikSectionsQuery;

/**
 * @author meanmail
 */
public class StepikSectionsAction extends StepikAbstractAction {
    public StepikSectionsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikSectionsQuery get() {
        return new StepikSectionsQuery(this);
    }
}
