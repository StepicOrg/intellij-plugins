package org.stepik.api.objects.lessons;

import org.stepik.api.objects.ObjectsContainer;

import java.util.List;

/**
 * @author meanmail
 */
public class Lessons extends ObjectsContainer {
    private List<Lesson> lessons;

    public int getCount() {
        if (lessons == null) {
            return 0;
        }

        return lessons.size();
    }

    public List<Lesson> getLessons() {
        return lessons;
    }
}
