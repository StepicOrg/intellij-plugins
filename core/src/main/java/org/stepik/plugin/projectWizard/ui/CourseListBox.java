package org.stepik.plugin.projectWizard.ui;

import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.SupportedLanguages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.StudyObject;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * @author meanmail
 */
public class CourseListBox extends JComboBox<StudyObject> {
    private final CourseListModel courseListModel;
    private ProjectSetting target;
    private SupportedLanguages programmingLanguage = SupportedLanguages.INVALID;

    public CourseListBox() {
        super();
        courseListModel = new CourseListModel();
        setModel(courseListModel);
        CourseListBoxEditor courseEditor = new CourseListBoxEditor();
        courseEditor.setModel(courseListModel);
        courseEditor.setOwner(this);
        setEditor(courseEditor);
    }

    void refresh(@NotNull SupportedLanguages programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
        courseListModel.update(programmingLanguage);
    }

    void refresh(@NotNull Project project) {
        courseListModel.update(programmingLanguage);
    }

    void setTarget(@Nullable ProjectSetting target) {
        this.target = target;
    }

    @NotNull
    @Override
    public StudyObject getSelectedItem() {
        return courseListModel.getSelectedItem();
    }

    @Override
    protected void fireItemStateChanged(@NotNull ItemEvent e) {
        super.fireItemStateChanged(e);

        if (target != null && e.getStateChange() == ItemEvent.SELECTED) {
            target.selectedStudyNode((StudyObject) e.getItem());
        }
    }
}
