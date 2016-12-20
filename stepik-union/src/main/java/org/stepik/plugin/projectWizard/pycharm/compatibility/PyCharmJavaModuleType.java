package org.stepik.plugin.projectWizard.pycharm.compatibility;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;

import javax.swing.*;

public class PyCharmJavaModuleType extends ModuleType<PyCharmJavaModuleBuilder> {
    static final PyCharmJavaModuleType JAVA_MODULE;
    private static final String MODULE_NAME = "Stepik Union Java module type";
    private static final String ID = "JAVA_MODULE";

    static {
        try {
            JAVA_MODULE = (PyCharmJavaModuleType) Class.forName(
                    "org.stepik.plugin.projectWizard.pycharm.compatibility.PyCharmJavaModuleType").newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public PyCharmJavaModuleType() {
        super(ID);
    }

    private static boolean isValidJavaSdk(@NotNull Module module) {
        return ModuleRootManager.getInstance(module)
                .getSourceRoots(JavaModuleSourceRootTypes.SOURCES)
                .isEmpty();
    }

    @NotNull
    public PyCharmJavaModuleBuilder createModuleBuilder() {
        return new PyCharmJavaModuleBuilder();
    }

    @NotNull
    public String getName() {
        return MODULE_NAME;
    }

    @NotNull
    public String getDescription() {
        return "Stepik Java Module Type";
    }

    public Icon getBigIcon() {
        return AllIcons.Modules.Types.JavaModule;
    }

    public Icon getNodeIcon(boolean isOpened) {
        return AllIcons.Nodes.Module;
    }

    public boolean isValidSdk(@NotNull Module module, Sdk projectSdk) {
        return isValidJavaSdk(module);
    }
}