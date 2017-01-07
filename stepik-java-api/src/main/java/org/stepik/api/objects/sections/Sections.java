package org.stepik.api.objects.sections;

import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Sections extends ObjectsContainer {
    private List<Section> sections;

    public List<Section> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    @Override
    protected List getItems() {
        return getSections();
    }
}
