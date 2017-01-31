package org.stepik.plugin.projectWizard.ui;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.courses.Course;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * @author meanmail
 */
public class CourseListBox extends JComboBox<Course> {
    private final CourseListModel courseListModel;
    private ProjectSetting target;

    public CourseListBox() {
        super();
        courseListModel = new CourseListModel();
        setModel(courseListModel);
        CourseListBoxEditor courseEditor = new CourseListBoxEditor();
        courseEditor.setModel(courseListModel);
        courseEditor.setOwner(this);
        setEditor(courseEditor);
    }

    void refresh(@NotNull Project project) {
        courseListModel.update(project);
    }

    void setTarget(@Nullable ProjectSetting target) {
        this.target = target;
    }

    @NotNull
    @Override
    public Course getSelectedItem() {
        return courseListModel.getSelectedItem();
    }

    @Override
    protected void fireItemStateChanged(@NotNull ItemEvent e) {
        super.fireItemStateChanged(e);

        if (target != null && e.getStateChange() == ItemEvent.SELECTED) {
            target.selectedCourse((Course) e.getItem());
        }
    }
}
