package org.stepik.plugin.projectWizard.idea;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;

import javax.swing.*;

public class StepikModuleType extends ModuleType<CourseModuleBuilder> {
    static final String MODULE_NAME = "Stepik";
    static final StepikModuleType STEPIK_MODULE_TYPE;
    private static final String ID = "STEPIK_MODULE_TYPE";

    static {
        STEPIK_MODULE_TYPE = instantiate();
    }

    public StepikModuleType() {
        super(ID);
    }

    @NotNull
    private static StepikModuleType instantiate() {
        try {
            return (StepikModuleType) Class.forName("org.stepik.plugin.projectWizard.idea.StepikModuleType")
                    .newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static boolean isValidJavaSdk(@NotNull Module module) {
        //noinspection SimplifiableIfStatement
        if (ModuleRootManager.getInstance(module).getSourceRoots(JavaModuleSourceRootTypes.SOURCES).isEmpty())
            return true;
        return JavaPsiFacade.getInstance(module.getProject()).findClass(CommonClassNames.JAVA_LANG_OBJECT,
                module.getModuleWithLibrariesScope()) != null;
    }

    @NotNull
    @Override
    public CourseModuleBuilder createModuleBuilder() {
        return new CourseModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Stepik Module Type";
    }

    @Override
    public Icon getBigIcon() {
        return AllStepikIcons.stepikLogoBig;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return AllStepikIcons.stepikLogo;
    }

    @Nullable
    @Override
    public ModuleWizardStep modifyProjectTypeStep(
            @NotNull SettingsStep settingsStep,
            @NotNull final ModuleBuilder moduleBuilder) {
        return ProjectWizardStepFactory.getInstance()
                .createJavaSettingsStep(settingsStep, moduleBuilder, moduleBuilder::isSuitableSdkType);
    }

    @Override
    public boolean isValidSdk(@NotNull final Module module, final Sdk projectSdk) {
        return isValidJavaSdk(module);
    }
}