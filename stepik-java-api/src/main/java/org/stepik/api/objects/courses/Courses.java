package org.stepik.api.objects.courses;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Courses extends ObjectsContainer<Course> {
    private List<Course> courses;

    @NotNull
    public List<Course> getCourses() {
        if (courses == null) {
            courses = new ArrayList<>();
        }
        return courses;
    }

    @NotNull
    @Override
    public List<Course> getItems() {
        return getCourses();
    }

    @NotNull
    @Override
    public Class<Course> getItemClass() {
        return Course.class;
    }
}
