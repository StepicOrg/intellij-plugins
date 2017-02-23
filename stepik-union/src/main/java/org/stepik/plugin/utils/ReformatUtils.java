package org.stepik.plugin.utils;

import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ReformatUtils {

    public static void reformatSelectedEditor(@NotNull Project project, @NotNull Document document) {
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null)
            return;

        new ReformatCodeProcessor(new OptimizeImportsProcessor(project, file), false).run();
    }
}