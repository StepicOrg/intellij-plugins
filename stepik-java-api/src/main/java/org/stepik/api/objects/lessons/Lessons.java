package org.stepik.api.objects.lessons;

import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Lessons extends ObjectsContainer {
    private List<Lesson> lessons;

    public List<Lesson> getLessons() {
        if (lessons == null) {
            lessons = new ArrayList<>();
        }
        return lessons;
    }

    @Override
    protected List getItems() {
        return getLessons();
    }
}
