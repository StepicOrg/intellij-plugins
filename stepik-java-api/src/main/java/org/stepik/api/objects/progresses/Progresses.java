package org.stepik.api.objects.progresses;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Progresses extends ObjectsContainer {
    private List<Progress> progresses;

    @NotNull
    @Override
    protected List getItems() {
        return getProgresses();
    }

    @NotNull
    public List<Progress> getProgresses() {
        if (progresses == null) {
            progresses = new ArrayList<>();
        }
        return progresses;
    }
}
