package org.stepik.plugin.projectWizard.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;

import static org.stepik.plugin.projectWizard.ui.BuildType.COURSE_LINK;
import static org.stepik.plugin.projectWizard.ui.BuildType.COURSE_LIST;

/**
 * @author meanmail
 */
public class BuildTypeComboBox extends JComboBox<BuildType> {
    private ProjectSetting target;

    public BuildTypeComboBox() {
        addItem(COURSE_LIST);
        addItem(COURSE_LINK);
        setSelectedItem(COURSE_LIST);
    }

    void setTarget(@Nullable ProjectSetting target) {
        this.target = target;
    }

    @Override
    protected void fireItemStateChanged(@NotNull ItemEvent e) {
        super.fireItemStateChanged(e);

        if (e.getStateChange() == ItemEvent.SELECTED && target != null) {
            target.selectedBuildType((BuildType) e.getItem());
        }
    }

    @NotNull
    @Override
    public BuildType getSelectedItem() {
        return (BuildType) super.getSelectedItem();
    }
}
