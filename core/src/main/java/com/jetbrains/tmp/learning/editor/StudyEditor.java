package com.jetbrains.tmp.learning.editor;

import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of StudyEditor which has panel with special buttons and step text
 * also @see {@link StudyFileEditorProvider}
 */
public class StudyEditor extends PsiAwareTextEditorImpl {
    private final StepFile stepFile;

    StudyEditor(@NotNull final Project project, @NotNull final VirtualFile file) {
        super(project, file, TextEditorProvider.getInstance());
        stepFile = StudyUtils.getStepFile(project, file);
    }

    public StepFile getStepFile() {
        return stepFile;
    }
}
