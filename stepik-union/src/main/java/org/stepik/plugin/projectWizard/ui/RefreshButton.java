package org.stepik.plugin.projectWizard.ui;

import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author meanmail
 */
public class RefreshButton extends JButton {
    private CourseListBox target;

    public RefreshButton() {
        setIcon(AllIcons.Actions.Refresh);
    }

    void setTarget(@NotNull CourseListBox target) {
        this.target = target;
    }

    @Override
    protected void fireActionPerformed(ActionEvent event) {
        super.fireActionPerformed(event);

        if (target != null) {
            target.refresh();
        }
    }
}
