package org.stepik.plugin.java;

import com.intellij.ide.util.projectWizard.AbstractModuleBuilder;
import com.intellij.openapi.ui.ValidationInfo;
import com.jetbrains.edu.intellij.EduIntelliJProjectTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.plugin.java.project.wizard.JavaCourseBuilder;

import javax.swing.*;

public class StepikJavaProjectTemplate implements EduIntelliJProjectTemplate {

    @NotNull
    @Override
    public String getName() {
        return "Stepik Java";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "Generate project template on some programming builders at Stepik.org";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public AbstractModuleBuilder createModuleBuilder() {
        return new JavaCourseBuilder();
    }

    @Nullable
    @Override
    public ValidationInfo validateSettings() {
        return null;
    }


}
