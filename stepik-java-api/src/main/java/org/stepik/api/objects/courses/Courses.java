package org.stepik.api.objects.courses;

import org.stepik.api.objects.ObjectsContainer;

import java.util.List;

/**
 * @author meanmail
 */
public class Courses extends ObjectsContainer {
    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public int getCount() {
        if (courses == null) {
            return 0;
        }

        return courses.size();
    }
}
