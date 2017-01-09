package org.stepik.api.objects.steps;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Steps extends ObjectsContainer {
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
    protected List getItems() {
        return getSteps();
    }
}
