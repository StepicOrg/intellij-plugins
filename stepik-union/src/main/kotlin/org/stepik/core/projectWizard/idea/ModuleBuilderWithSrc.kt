package org.stepik.core.projectWizard.idea

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File


open class ModuleBuilderWithSrc : AbstractModuleBuilder() {
    @Throws(ConfigurationException::class)
    override fun setupRootModel(rootModel: ModifiableRootModel) {
        super.setupRootModel(rootModel)

        val contentEntry = this.doAddContentEntry(rootModel)
        val moduleLibraryPath: String
        if (contentEntry != null) {
            moduleLibraryPath = this.contentEntryPath + File.separator + "src"

            File(moduleLibraryPath).mkdirs()
            val localFS = LocalFileSystem.getInstance()
            val name = FileUtil.toSystemIndependentName(moduleLibraryPath)
            val sourceLibraryPath = localFS.refreshAndFindFileByPath(name)
            if (sourceLibraryPath != null) {
                contentEntry.addSourceFolder(sourceLibraryPath, false, "")
            }
        }
    }
}
