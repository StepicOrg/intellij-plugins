package org.stepik.api.objects.enrollments;

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
public class Enrollments extends ObjectsContainer {
    private List<Enrollment> enrollments;
    private List<Assignment> assignments;
    private List<Course> courses;
    private List<Section> sections;
    private List<Unit> units;
    private List<Lesson> lessons;
    private List<Progress> progresses;

    public List<Enrollment> getEnrollments() {
        if (enrollments == null) {
            enrollments = new ArrayList<>();
        }
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    @Override
    protected List getItems() {
        return enrollments;
    }

    public List<Assignment> getAssignments() {
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<Course> getCourses() {
        if (courses == null) {
            courses = new ArrayList<>();
        }
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Section> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Unit> getUnits() {
        if (units == null) {
            units = new ArrayList<>();
        }
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public List<Lesson> getLessons() {
        if (lessons == null) {
            lessons = new ArrayList<>();
        }
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Progress> getProgresses() {
        if (progresses == null) {
            progresses = new ArrayList<>();
        }
        return progresses;
    }

    public void setProgresses(List<Progress> progresses) {
        this.progresses = progresses;
    }
}
