package org.stepik.api.objects.sections;

import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Sections extends ObjectsContainer {
    private List<Section> sections;

    public int getCount() {
        return sections.size();
    }

    public List<Section> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }
}
