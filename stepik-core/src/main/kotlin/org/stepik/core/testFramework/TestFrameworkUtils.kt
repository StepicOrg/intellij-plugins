package org.stepik.core.testFramework

import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.utils.runWriteActionAndWait
import java.io.IOException

fun createDirectories(parent: VirtualFile, directoryRelativePath: String): VirtualFile? {
    return getApplication().runWriteActionAndWait {
        return@runWriteActionAndWait directoryRelativePath.split('/')
                .fold(parent) { dir, part ->
                    try {
                        return@fold dir.findChild(part) ?: dir.createChildDirectory(null, part)
                    } catch (e: IOException) {
                        return@runWriteActionAndWait null
                    }
                }
    }
}
