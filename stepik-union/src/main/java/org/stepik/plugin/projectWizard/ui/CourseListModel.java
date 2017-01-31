package org.stepik.plugin.projectWizard.ui;

import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.courses.Courses;
import org.stepik.plugin.projectWizard.StepikProjectGenerator;
import org.stepik.plugin.utils.Utils;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author meanmail
 */
class CourseListModel extends AbstractListModel<Course> implements ComboBoxModel<Course>, Serializable {
    private final List<Course> courses = new ArrayList<>();
    private Object selectedItem;

    @Override
    public int getSize() {
        return courses.size();
    }

    @NotNull
    @Override
    public Course getElementAt(int index) {
        if (index >= 0 && index < courses.size()) {
            return courses.get(index);
        } else {
            return StepikProjectGenerator.EMPTY_COURSE;
        }
    }

    void update(@NotNull Project project, @NotNull SupportedLanguages programmingLanguage) {
        List<Course> newCourseList = StepikProjectGenerator.getCoursesUnderProgress(project, programmingLanguage);
        Course selectedCourse = getSelectedItem();
        courses.clear();

        if (newCourseList.size() > 0) {
            courses.addAll(newCourseList);
            if (selectedCourse == StepikProjectGenerator.EMPTY_COURSE || !courses.contains(selectedCourse)) {
                selectedCourse = courses.get(0);
            }
        } else {
            courses.add(StepikProjectGenerator.EMPTY_COURSE);
        }

        setSelectedItem(selectedCourse);
        fireIntervalAdded(this, 0, getSize() - 1);
    }

    @NotNull
    @Override
    public Course getSelectedItem() {
        if (selectedItem == null || !(selectedItem instanceof Course)) {
            return StepikProjectGenerator.EMPTY_COURSE;
        }
        return (Course) selectedItem;
    }

    @Override
    public void setSelectedItem(@Nullable Object anItem) {
        if (anItem != null && !(anItem instanceof Course)) {
            selectedItem = getCourse(anItem.toString());
        } else {
            selectedItem = anItem;
        }
        fireContentsChanged(this, -1, -1);
    }

    @NotNull
    private Object getCourse(@NotNull String link) {
        final String finalLink = link.toLowerCase();

        List<Course> filteredCourses = courses.stream()
                .filter(course -> course.getTitle().toLowerCase().equals(finalLink))
                .collect(Collectors.toList());

        if (filteredCourses.size() > 0) {
            return filteredCourses.get(0);
        }

        int courseId = Utils.getCourseIdFromLink(link);

        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

        Courses courses = null;

        if (courseId != 0) {
            courses = stepikApiClient.courses()
                    .get()
                    .id(courseId)
                    .execute();
        }

        Course course;

        if (courseId == 0 || courses.isEmpty()) {
            course = new Course();
            course.setTitle(link);
            course.setDescription("Wrong link");
        } else {
            course = courses.getCourses().get(0);
        }

        return course;
    }

    public List<Course> getCourses() {
        return courses;
    }
}
