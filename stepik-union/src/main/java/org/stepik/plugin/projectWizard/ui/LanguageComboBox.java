package org.stepik.plugin.projectWizard.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.SupportedLanguages;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;

/**
 * @author meanmail
 */
public class LanguageComboBox extends JComboBox<SupportedLanguages> {
    private ProjectSetting target;

    public LanguageComboBox() {
        Arrays.stream(SupportedLanguages.values())
                .filter(language -> language != SupportedLanguages.INVALID)
                .forEach(this::addItem);
        setSelectedItem(SupportedLanguages.JAVA8);
    }

    void setTarget(@Nullable ProjectSetting target) {
        this.target = target;
    }

    @NotNull
    @Override
    public SupportedLanguages getSelectedItem() {
        SupportedLanguages language = (SupportedLanguages) super.getSelectedItem();
        if (language == null) {
            language = SupportedLanguages.INVALID;
        }
        return language;
    }

    @Override
    protected void fireItemStateChanged(@NotNull ItemEvent e) {
        super.fireItemStateChanged(e);

        if (target != null && e.getStateChange() == ItemEvent.SELECTED) {
            target.selectedProgrammingLanguage((SupportedLanguages) e.getItem());
        }
    }
}
