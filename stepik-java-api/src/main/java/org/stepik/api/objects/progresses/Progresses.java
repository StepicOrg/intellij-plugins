package org.stepik.api.objects.progresses;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Progresses extends ObjectsContainer<Progress> {
    private List<Progress> progresses;

    @NotNull
    @Override
    public List<Progress> getItems() {
        return getProgresses();
    }

    @NotNull
    @Override
    public Class<Progress> getItemClass() {
        return Progress.class;
    }

    @NotNull
    public List<Progress> getProgresses() {
        if (progresses == null) {
            progresses = new ArrayList<>();
        }
        return progresses;
    }
}
