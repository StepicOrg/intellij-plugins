package org.stepik.hyperskill.projectWizard.idea

import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File

open class ModuleBuilderWithSrc : AbstractModuleBuilder() {
    override fun setupRootModel(rootModel: ModifiableRootModel) {
        super.setupRootModel(rootModel)
        
        val contentEntry = this.doAddContentEntry(rootModel) ?: return
        
        val moduleLibraryPath = "${this.contentEntryPath}${File.separator}src"
        
        File(moduleLibraryPath).mkdirs()
        val localFS = LocalFileSystem.getInstance()
        val name = FileUtil.toSystemIndependentName(moduleLibraryPath)
        val sourceLibraryPath = localFS.refreshAndFindFileByPath(name) ?: return
        contentEntry.addSourceFolder(sourceLibraryPath, false, "")
    }
}
