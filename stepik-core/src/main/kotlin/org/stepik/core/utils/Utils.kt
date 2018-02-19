package org.stepik.core.utils

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.courses.Course

object Utils {
    val currentProject: Project
        get() {
            val projectManger = ProjectManager.getInstance()
            return if (projectManger.openProjects.isEmpty()) {
                projectManger.defaultProject
            } else {
                projectManger.openProjects[0]
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

val EMPTY_STUDY_OBJECT = initEmptyStudyNode()

private fun initEmptyStudyNode(): StudyObject {
    val course = Course()
    course.setTitle("Empty")
    course.setDescription("Please, press refresh button")
    return course
}
