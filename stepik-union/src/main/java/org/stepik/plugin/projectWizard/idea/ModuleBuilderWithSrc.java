package org.stepik.plugin.projectWizard.idea;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

/**
 * @author meanmail
 */
class ModuleBuilderWithSrc extends AbstractModuleBuilder  {
    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        super.setupRootModel(rootModel);

        ContentEntry contentEntry = this.doAddContentEntry(rootModel);
        String moduleLibraryPath;
        if (contentEntry != null) {
            moduleLibraryPath = this.getContentEntryPath() + File.separator + "src";
            //noinspection ResultOfMethodCallIgnored
            (new File(moduleLibraryPath)).mkdirs();
            LocalFileSystem localFS = LocalFileSystem.getInstance();
            String name = FileUtil.toSystemIndependentName(moduleLibraryPath);
            VirtualFile sourceLibraryPath = localFS.refreshAndFindFileByPath(name);
            if (sourceLibraryPath != null) {
                contentEntry.addSourceFolder(sourceLibraryPath, false, "");
            }
        }
    }
}
