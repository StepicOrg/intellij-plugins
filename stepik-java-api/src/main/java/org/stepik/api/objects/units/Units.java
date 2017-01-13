package org.stepik.api.objects.units;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Units extends ObjectsContainer<Unit> {
    private List<Unit> units;

    @NotNull
    public List<Unit> getUnits() {
        if (units == null) {
            units = new ArrayList<>();
        }
        return units;
    }

    @NotNull
    @Override
    public List<Unit> getItems() {
        return getUnits();
    }

    @NotNull
    @Override
    public Class<Unit> getItemClass() {
        return Unit.class;
    }
}
