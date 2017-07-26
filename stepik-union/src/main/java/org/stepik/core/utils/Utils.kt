package org.stepik.core.utils

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager

object Utils {
    val currentProject: Project
        get() {
            val projectManger = ProjectManager.getInstance()
            if (projectManger.openProjects.isEmpty()) {
                return projectManger.defaultProject
            } else {
                return projectManger.openProjects[0]
            }
        }

    val isCanceled: Boolean
        get() {
            try {
                ProgressManager.checkCanceled()
            } catch (e: ProcessCanceledException) {
                return true
            }

            return false
        }

    fun saveAllDocuments(project: Project) {
        val documentManager = FileDocumentManager.getInstance()
        FileEditorManager.getInstance(project).openFiles
                .mapNotNull { documentManager.getDocument(it) }
                .forEach { documentManager.saveDocument(it) }
    }
}
