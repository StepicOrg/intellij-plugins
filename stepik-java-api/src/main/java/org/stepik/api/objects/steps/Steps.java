package org.stepik.api.objects.steps;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Steps extends ObjectsContainer<Step> {
    private List<Step> steps;

    @NotNull
    public List<Step> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }

    @NotNull
    @Override
    public List<Step> getItems() {
        return getSteps();
    }

    @NotNull
    @Override
    public Class<Step> getItemClass() {
        return Step.class;
    }
}
