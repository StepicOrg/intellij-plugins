package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class PyCharmJavaModuleBuilder extends ModuleBuilder {

    public PyCharmJavaModuleBuilder() {
    }

    public ModuleType getModuleType() {
        return PyCharmJavaModuleType.JAVA_MODULE;
    }

    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof JavaSdkType;
    }

    @Nullable
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return PyCharmJavaModuleType.JAVA_MODULE.modifySettingsStep(settingsStep, this);
    }

    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        CompilerModuleExtension compilerModuleExtension = rootModel.getModuleExtension(CompilerModuleExtension.class);
        compilerModuleExtension.setExcludeOutput(true);
        if (this.myJdk != null) {
            rootModel.setSdk(this.myJdk);
        } else {
            rootModel.inheritSdk();
        }

        compilerModuleExtension.inheritCompilerOutputPath(true);
    }

    public int getWeight() {
        return 100;
    }
}
