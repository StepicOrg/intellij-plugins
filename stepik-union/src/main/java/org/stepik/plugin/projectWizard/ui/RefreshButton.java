package org.stepik.plugin.projectWizard.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author meanmail
 */
public class RefreshButton extends JButton {
    private CourseListBox target;
    private Project project;

    public RefreshButton() {
        setIcon(AllIcons.Actions.Refresh);
    }

    void setTarget(@NotNull CourseListBox target, @NotNull Project project) {
        this.target = target;
        this.project = project;
    }

    @Override
    protected void fireActionPerformed(ActionEvent event) {
        super.fireActionPerformed(event);

        if (target != null) {
            target.refresh(project);
        }
    }
}
