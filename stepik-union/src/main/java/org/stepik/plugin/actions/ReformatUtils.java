package org.stepik.plugin.actions;

import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.jetbrains.tmp.learning.StudyUtils;
import org.jetbrains.annotations.NotNull;

public class ReformatUtils {

    public static void reformatSelectedEditor(@NotNull Project project) {
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        final Editor editor = StudyUtils.getSelectedEditor(project);
        if (editor == null)
            return;

        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (file == null)
            return;

        new ReformatCodeProcessor(new OptimizeImportsProcessor(project, file), false).run();
    }
}