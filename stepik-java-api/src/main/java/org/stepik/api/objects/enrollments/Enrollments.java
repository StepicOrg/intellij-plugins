package org.stepik.api.objects.enrollments;

import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Enrollments extends ObjectsContainer {
    private List<Enrollment> enrollments;

    public List<Enrollment> getEnrollments() {
        if (enrollments == null) {
            enrollments = new ArrayList<>();
        }
        return enrollments;
    }

    @Override
    protected List getItems() {
        return null;
    }
}
