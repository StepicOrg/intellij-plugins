package org.stepik.api.queries.sections;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikSectionsQuery extends StepikAbstractGetQuery<Sections> {
    public StepikSectionsQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Sections.class);
    }

    public StepikSectionsQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikSectionsQuery id(int[] values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikSectionsQuery id(int values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikSectionsQuery page(int value) {
        addParam("page", value);
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.SECTIONS;
    }
}
