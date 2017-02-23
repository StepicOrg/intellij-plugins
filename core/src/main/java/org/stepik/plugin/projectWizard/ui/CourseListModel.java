package org.stepik.plugin.projectWizard.ui;

import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.SupportedLanguages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.StudyObject;
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
class CourseListModel extends AbstractListModel<StudyObject> implements ComboBoxModel<StudyObject>, Serializable {
    private final List<StudyObject> courses = new ArrayList<>();
    private Object selectedItem;

    @Override
    public int getSize() {
        return courses.size();
    }

    @NotNull
    @Override
    public StudyObject getElementAt(int index) {
        if (index >= 0 && index < courses.size()) {
            return courses.get(index);
        } else {
            return StepikProjectGenerator.EMPTY_STUDY_NODE;
        }
    }

    void update(@NotNull Project project, @NotNull SupportedLanguages programmingLanguage) {
        List<StudyObject> newCourseList = StepikProjectGenerator.getCoursesUnderProgress(project, programmingLanguage);
        StudyObject selectedCourse = getSelectedItem();
        courses.clear();

        if (!newCourseList.isEmpty()) {
            courses.addAll(newCourseList);
            if (selectedCourse == StepikProjectGenerator.EMPTY_STUDY_NODE || !courses.contains(selectedCourse)) {
                selectedCourse = courses.get(0);
            }
        } else {
            courses.add(StepikProjectGenerator.EMPTY_STUDY_NODE);
        }

        setSelectedItem(selectedCourse);
        fireIntervalAdded(this, 0, getSize() - 1);
    }

    @NotNull
    @Override
    public StudyObject getSelectedItem() {
        if (selectedItem == null || !(selectedItem instanceof StudyObject)) {
            return StepikProjectGenerator.EMPTY_STUDY_NODE;
        }
        return (StudyObject) selectedItem;
    }

    @Override
    public void setSelectedItem(@Nullable Object anItem) {
        if (anItem != null && !(anItem instanceof StudyObject)) {
            selectedItem = getCourse(anItem.toString());
        } else {
            selectedItem = anItem;
        }
        fireContentsChanged(this, -1, -1);
    }

    @NotNull
    private Object getCourse(@NotNull String link) {
        final String finalLink = link.toLowerCase();

        List<StudyObject> filteredCourses = courses.stream()
                .filter(studyObject -> studyObject.getTitle().toLowerCase().equals(finalLink))
                .collect(Collectors.toList());

        if (!filteredCourses.isEmpty()) {
            return filteredCourses.get(0);
        }

        return Utils.getStudyObjectFromLink(link);
    }

    List<StudyObject> getCourses() {
        return courses;
    }
}
