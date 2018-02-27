package org.stepik.core.testFramework

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.utils.runWriteActionAndWait

fun createDirectory(parent: VirtualFile, directoryName: String): VirtualFile? {
    return getApplication().runWriteActionAndWait {
        parent.createChildDirectory(null, directoryName)
    }
}

fun createDirectories(application: Application, parent: VirtualFile, directoryRelativePath: String): VirtualFile? {
    return application.runWriteActionAndWait {
        return@runWriteActionAndWait directoryRelativePath.split('/')
                .fold(parent) { dir, part ->
                    return@fold dir.findChild(part) ?: dir.createChildDirectory(null, part)
                }
    }
}
