package org.stepik.plugin.collective.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsConfigurableProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepikSettingsConfigurable implements SearchableConfigurable, VcsConfigurableProvider {
    private StepikSettingsPanel settingsPane;

    public StepikSettingsConfigurable() {
    }

    @NotNull
    public String getDisplayName() {
        return "Stepik";
    }

    @NotNull
    public String getHelpTopic() {
        return "settings.stepik";
    }

    @NotNull
    public JComponent createComponent() {
        if (settingsPane == null) {
            settingsPane = new StepikSettingsPanel();
        }
        return settingsPane.getPanel();
    }

    public boolean isModified() {
        return settingsPane != null && settingsPane.isModified();
    }

    public void apply() throws ConfigurationException {
        if (settingsPane != null) {
            settingsPane.apply();
        }
    }

    public void reset() {
        if (settingsPane != null) {
            settingsPane.reset();
        }
    }

    public void disposeUIResources() {
        settingsPane = null;
    }

    @NotNull
    public String getId() {
        return getHelpTopic();
    }

    public Runnable enableSearch(String option) {
        return null;
    }

    @Nullable
    @Override
    public Configurable getConfigurable(Project project) {
        return this;
    }
}
