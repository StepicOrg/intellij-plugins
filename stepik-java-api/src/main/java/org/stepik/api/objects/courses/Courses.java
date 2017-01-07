package org.stepik.api.objects.courses;

import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Courses extends ObjectsContainer {
    private List<Course> courses;

    public List<Course> getCourses() {
        if (courses == null) {
            courses = new ArrayList<>();
        }
        return courses;
    }

    @Override
    protected List getItems() {
        return getCourses();
    }
}
