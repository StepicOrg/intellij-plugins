package org.stepik.api.objects.enrollments;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class EnrollmentsPost {
    private EnrollmentPost enrollment;

    @NotNull
    public EnrollmentPost getEnrollment() {
        if (enrollment == null) {
            enrollment = new EnrollmentPost();
        }
        return enrollment;
    }
}
