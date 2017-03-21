package org.stepik.plugin.projectWizard.ui;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.stepik.plugin.projectWizard.StepikProjectGenerator.EMPTY_STUDY_OBJECT;

/**
 * @author meanmail
 */
class CourseListModel extends AbstractListModel<StudyObject> implements ComboBoxModel<StudyObject>, Serializable {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
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
            return EMPTY_STUDY_OBJECT;
        }
    }

    void update(@NotNull SupportedLanguages programmingLanguage) {
        executor.execute(() -> {
            List<StudyObject> newCourseList = StepikProjectGenerator.getCourses(programmingLanguage);

            StudyObject selectedCourse = getSelectedItem();
            courses.clear();

            if (!newCourseList.isEmpty()) {
                courses.addAll(newCourseList);
                if (selectedCourse == EMPTY_STUDY_OBJECT || !courses.contains(selectedCourse)) {
                    selectedCourse = courses.get(0);
                }
            } else {
                courses.add(EMPTY_STUDY_OBJECT);
            }

            StudyObject finalSelectedCourse = selectedCourse;
            SwingUtilities.invokeLater(() -> {
                setSelectedItem(finalSelectedCourse);
                fireIntervalAdded(this, 0, getSize() - 1);
            });
        });
    }

    @NotNull
    @Override
    public StudyObject getSelectedItem() {
        if (!(selectedItem instanceof StudyObject)) {
            return EMPTY_STUDY_OBJECT;
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
