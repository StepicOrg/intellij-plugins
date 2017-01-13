package org.stepik.plugin.projectWizard.ui;

import com.jetbrains.tmp.learning.SupportedLanguages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;

/**
 * @author meanmail
 */
public class LanguageComboBox extends JComboBox<SupportedLanguages> {
    public LanguageComboBox() {
        addItem(SupportedLanguages.PYTHON);
        addItem(SupportedLanguages.JAVA);
        Arrays.stream(SupportedLanguages.values())
                .filter(language -> language != SupportedLanguages.INVALID)
                .forEach(this::addItem);
        setSelectedItem(SupportedLanguages.JAVA);
    }

    @NotNull
    @Override
    public SupportedLanguages getSelectedItem() {
        return (SupportedLanguages) super.getSelectedItem();
    }
}
