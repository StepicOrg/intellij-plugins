package org.stepik.api.objects.progresses;

import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Progresses extends ObjectsContainer {
    private List<Progress> progresses;

    @Override
    protected List getItems() {
        return null;
    }

    public List<Progress> getProgresses() {
        if (progresses == null) {
            progresses = new ArrayList<>();
        }
        return progresses;
    }
}
