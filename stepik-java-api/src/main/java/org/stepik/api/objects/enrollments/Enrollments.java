package org.stepik.api.objects.enrollments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.ObjectsContainer;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.lessons.Lesson;
import org.stepik.api.objects.progresses.Progress;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.units.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Enrollments extends ObjectsContainer<Enrollment> {
    private List<Enrollment> enrollments;
    private List<Assignment> assignments;
    private List<Course> courses;
    private List<Section> sections;
    private List<Unit> units;
    private List<Lesson> lessons;
    private List<Progress> progresses;

    @NotNull
    public List<Enrollment> getEnrollments() {
        if (enrollments == null) {
            enrollments = new ArrayList<>();
        }
        return enrollments;
    }

    public void setEnrollments(@Nullable List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    @NotNull
    @Override
    public List<Enrollment> getItems() {
        return getEnrollments();
    }

    @NotNull
    @Override
    public Class<Enrollment> getItemClass() {
        return Enrollment.class;
    }

    @NotNull
    public List<Assignment> getAssignments() {
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        return assignments;
    }

    public void setAssignments(@Nullable List<Assignment> assignments) {
        this.assignments = assignments;
    }

    @NotNull
    public List<Course> getCourses() {
        if (courses == null) {
            courses = new ArrayList<>();
        }
        return courses;
    }

    public void setCourses(@Nullable List<Course> courses) {
        this.courses = courses;
    }

    @NotNull
    public List<Section> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    public void setSections(@Nullable List<Section> sections) {
        this.sections = sections;
    }

    @NotNull
    public List<Unit> getUnits() {
        if (units == null) {
            units = new ArrayList<>();
        }
        return units;
    }

    public void setUnits(@Nullable List<Unit> units) {
        this.units = units;
    }

    @NotNull
    public List<Lesson> getLessons() {
        if (lessons == null) {
            lessons = new ArrayList<>();
        }
        return lessons;
    }

    public void setLessons(@Nullable List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @NotNull
    public List<Progress> getProgresses() {
        if (progresses == null) {
            progresses = new ArrayList<>();
        }
        return progresses;
    }

    public void setProgresses(@Nullable List<Progress> progresses) {
        this.progresses = progresses;
    }
}
