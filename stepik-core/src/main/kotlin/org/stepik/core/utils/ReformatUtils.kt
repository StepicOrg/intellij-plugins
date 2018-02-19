package org.stepik.core.utils

import com.intellij.codeInsight.actions.OptimizeImportsProcessor
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager

object ReformatUtils {

    fun reformatSelectedEditor(project: Project, document: Document) {
        PsiDocumentManager.getInstance(project).commitAllDocuments()

        val file = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        ReformatCodeProcessor(OptimizeImportsProcessor(project, file), false).run()
    }
}
