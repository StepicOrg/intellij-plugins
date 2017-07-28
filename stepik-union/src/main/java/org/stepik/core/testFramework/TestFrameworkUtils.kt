package org.stepik.core.testFramework

import com.intellij.openapi.application.Application
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile

fun createDirectory(application: Application, parent: VirtualFile, directoryName: String): VirtualFile? {
    var directory: VirtualFile? = null
    application.invokeAndWait {
        directory = application.runWriteAction(Computable<VirtualFile> {
            parent.createChildDirectory(null, directoryName)
        })
    }

    return directory
}

fun createDirectories(application: Application, parent: VirtualFile, directoryRelativePath: String): VirtualFile? {
    var directory: VirtualFile? = null
    application.invokeAndWait {
        directory = application.runWriteAction(Computable<VirtualFile> {
            val directories = directoryRelativePath.split('/')
            var newDirectory = parent
            directories.forEach {
                newDirectory = newDirectory.createChildDirectory(null, it)
            }

            return@Computable newDirectory
        })
    }

    return directory
}
