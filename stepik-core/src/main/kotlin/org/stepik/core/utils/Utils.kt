package org.stepik.core.utils

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
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
    course.title = "Empty"
    course.description = "Please, press refresh button"
    return course
}

fun <T> Set<T>.batch(n: Int): Sequence<List<T>> {
    return BatchingSequence(this.asSequence(), n)
}

private class BatchingSequence<out T>(val source: Sequence<T>, val batchSize: Int) : Sequence<List<T>> {
    override fun iterator(): Iterator<List<T>> = object : AbstractIterator<List<T>>() {
        val iterate = if (batchSize > 0) source.iterator() else emptyList<T>().iterator()
        override fun computeNext() {
            if (iterate.hasNext()) {
                setNext(iterate.asSequence().take(batchSize).toList())
            } else {
                done()
            }
        }
    }
}

fun refreshProjectView(project: Project?) {
    ApplicationManager.getApplication().invokeLater {
        if (project?.isDisposed == false) {
            ProjectView.getInstance(project).refresh()
        }
    }
}
