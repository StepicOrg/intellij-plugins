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
    private StepikSettingsPanel mySettingsPane;

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
        if (mySettingsPane == null) {
            mySettingsPane = new StepikSettingsPanel();
        }
        return mySettingsPane.getPanel();
    }

    public boolean isModified() {
        return mySettingsPane != null && mySettingsPane.isModified();
    }

    public void apply() throws ConfigurationException {
        if (mySettingsPane != null) {
            mySettingsPane.apply();
        }
    }

    public void reset() {
        if (mySettingsPane != null) {
            mySettingsPane.reset();
        }
    }

    public void disposeUIResources() {
        mySettingsPane = null;
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
