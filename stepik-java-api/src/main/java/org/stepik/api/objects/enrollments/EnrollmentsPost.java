package org.stepik.api.objects.enrollments;

/**
 * @author meanmail
 */
public class EnrollmentsPost {
    private EnrollmentPost enrollment;

    public EnrollmentPost getEnrollment() {
        if (enrollment == null) {
            enrollment = new EnrollmentPost();
        }
        return enrollment;
    }
}
