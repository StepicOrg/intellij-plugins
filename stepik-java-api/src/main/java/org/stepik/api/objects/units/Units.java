package org.stepik.api.objects.units;

import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Units extends ObjectsContainer {
    private List<Unit> units;

    public List<Unit> getUnits() {
        if (units == null) {
            units = new ArrayList<>();
        }
        return units;
    }

    @Override
    protected List getItems() {
        return getUnits();
    }
}
