package org.stepik.plugin.java.project.wizard;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import org.stepik.from.edu.intellij.utils.generation.StepikProjectGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;

import javax.swing.*;

public class StepikModuleType extends ModuleType<JavaCourseBuilder> {
    public static final String MODULE_NAME = "Stepik Union";
    public static final StepikModuleType STEPIK_MODULE_TYPE;
    private StepikProjectGenerator generator;

    static {
        STEPIK_MODULE_TYPE = (StepikModuleType) instantiate("org.stepik.plugin.java.project.wizard.StepikModuleType");
    }

    private static final String ID = "STEPIK_MODULE_TYPE";

    public StepikModuleType() {
        super(ID);
    }

    public static StepikModuleType getInstance() {
        return (StepikModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public JavaCourseBuilder createModuleBuilder() {
        return new JavaCourseBuilder();
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
        return IconLoader.getIcon("/icons/stepik_logo_green.png");
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return IconLoader.getIcon("/icons/stepik_logotype_13x13-2.png");
    }

//    @NotNull
//    @Override
//    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull JavaCourseBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider) {
////        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider);
//        ModuleWizardStep[] wizardSteps = new ModuleWizardStep[1];
//        wizardSteps[0] = new SelectCourseWizardStep(getGenerator(), wizardContext);
//        return wizardSteps;
//    }


    private StepikProjectGenerator getGenerator() {
        if (generator == null) {
            generator = new StepikProjectGenerator();
        }
        return generator;
    }

    @NotNull
    private static ModuleType instantiate(String className) {
        try {
            return (ModuleType) Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
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

    private static boolean isValidJavaSdk(@NotNull Module module) {
        if (ModuleRootManager.getInstance(module).getSourceRoots(JavaModuleSourceRootTypes.SOURCES).isEmpty())
            return true;
        return JavaPsiFacade.getInstance(module.getProject()).findClass(CommonClassNames.JAVA_LANG_OBJECT,
                module.getModuleWithLibrariesScope()) != null;
    }
}