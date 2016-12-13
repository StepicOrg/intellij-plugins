package org.stepik.plugin.projectWizard;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author meanmail
 */
abstract class AbstractModuleBuilder extends ModuleBuilder {
    @Override
    public ModuleType getModuleType() {
        return StepikModuleType.STEPIK_MODULE_TYPE;
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        CompilerModuleExtension compilerModuleExtension = rootModel.getModuleExtension(CompilerModuleExtension.class);
        compilerModuleExtension.setExcludeOutput(true);
        if (this.myJdk != null) {
            rootModel.setSdk(this.myJdk);
        } else {
            rootModel.inheritSdk();
        }

        compilerModuleExtension.inheritCompilerOutputPath(true);

        doAddContentEntry(rootModel);
    }

    @Override
    public String getPresentableName() {
        return StepikModuleType.MODULE_NAME;
    }
}