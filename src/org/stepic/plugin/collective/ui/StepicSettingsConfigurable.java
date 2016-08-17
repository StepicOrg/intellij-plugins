package org.stepic.plugin.collective.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsConfigurableProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepicSettingsConfigurable implements SearchableConfigurable, VcsConfigurableProvider {
  private StepicSettingsPanel mySettingsPane;

  public StepicSettingsConfigurable() {
  }

  @NotNull
  public String getDisplayName() {
    return "Stepic";
  }

  @NotNull
  public String getHelpTopic() {
    return "settings.stepic";
  }

  @NotNull
  public JComponent createComponent() {
    if (mySettingsPane == null) {
      mySettingsPane = new StepicSettingsPanel();
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
