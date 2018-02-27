package org.stepik.core.utils

import com.intellij.codeInsight.actions.OptimizeImportsProcessor
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager


fun Project.reformatDocument(document: Document) {
    PsiDocumentManager.getInstance(this).commitAllDocuments()

    val file = PsiDocumentManager.getInstance(this).getPsiFile(document) ?: return

    ReformatCodeProcessor(OptimizeImportsProcessor(this, file), false).run()
}
