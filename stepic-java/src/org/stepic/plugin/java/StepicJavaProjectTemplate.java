package org.stepic.plugin.java;

import com.intellij.ide.util.projectWizard.AbstractModuleBuilder;
import com.intellij.openapi.ui.ValidationInfo;
import com.jetbrains.edu.intellij.EduIntelliJProjectTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepic.plugin.java.project.wizard.StepicJavaModuleBuilder;

import javax.swing.*;

public class StepicJavaProjectTemplate implements EduIntelliJProjectTemplate {

    @NotNull
    @Override
    public String getName() {
        return "Stepic Java";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "Generate project template on some programming course at Stepic.org";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public AbstractModuleBuilder createModuleBuilder() {
        return new StepicJavaModuleBuilder();
    }

    @Nullable
    @Override
    public ValidationInfo validateSettings() {
        return null;
    }


}
