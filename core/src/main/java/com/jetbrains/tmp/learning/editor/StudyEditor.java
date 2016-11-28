package com.jetbrains.tmp.learning.editor;

import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of StudyEditor which has panel with special buttons and task text
 * also @see {@link StudyFileEditorProvider}
 */
public class StudyEditor extends PsiAwareTextEditorImpl {
    private final TaskFile myTaskFile;

    public StudyEditor(@NotNull final Project project, @NotNull final VirtualFile file) {
        super(project, file, TextEditorProvider.getInstance());
        myTaskFile = StudyUtils.getTaskFile(project, file);
    }

    public TaskFile getTaskFile() {
        return myTaskFile;
    }
}
