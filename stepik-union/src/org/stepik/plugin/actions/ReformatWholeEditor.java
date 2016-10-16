package org.stepik.plugin.actions;

import com.intellij.codeInsight.actions.AbstractLayoutCodeProcessor;
import com.intellij.codeInsight.actions.LastRunReformatCodeOptionsProvider;
import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.RearrangeCodeProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInsight.actions.ReformatCodeRunOptions;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.jetbrains.tmp.learning.StudyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReformatWholeEditor {

    public static void processCode(@NotNull Project project) {
        PsiDocumentManager.getInstance(project).commitAllDocuments();
        final Editor editor = StudyUtils.getSelectedEditor(project);

        PsiFile file;

        if (editor == null)
            return;

        file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (file == null)
            return;

        LastRunReformatCodeOptionsProvider provider = new LastRunReformatCodeOptionsProvider(PropertiesComponent.getInstance());
        ReformatCodeRunOptions currentRunOptions = provider.getLastRunOptions(file);

        currentRunOptions.setProcessingScope(null);

        AbstractLayoutCodeProcessor myProcessor = new OptimizeImportsProcessor(project, file);

        myProcessor = mixWithReformatProcessor(myProcessor, file);

        myProcessor.run();
    }

    private static AbstractLayoutCodeProcessor mixWithReformatProcessor(@Nullable AbstractLayoutCodeProcessor processor, PsiFile file) {
        if (processor != null) {
            processor = new ReformatCodeProcessor(processor, false);
        }
        else {
            processor = new ReformatCodeProcessor(file, false);
        }
        return processor;
    }

    private static AbstractLayoutCodeProcessor mixWithRearrangeProcessor(@NotNull AbstractLayoutCodeProcessor processor) {
        processor = new RearrangeCodeProcessor(processor);
        return processor;
    }
}