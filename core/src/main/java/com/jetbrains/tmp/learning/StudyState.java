package com.jetbrains.tmp.learning;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.courseFormat.Step;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import org.jetbrains.annotations.Nullable;

public class StudyState {
    private final StudyEditor studyEditor;
    private final Editor editor;
    private final StepFile stepFile;
    private final VirtualFile virtualFile;
    private final Step step;
    private final VirtualFile stepDir;

    public StudyState(@Nullable final StudyEditor studyEditor) {
        this.studyEditor = studyEditor;
        editor = studyEditor != null ? studyEditor.getEditor() : null;
        stepFile = studyEditor != null ? studyEditor.getStepFile() : null;
        virtualFile = editor != null ? FileDocumentManager.getInstance().getFile(editor.getDocument()) : null;
        stepDir = virtualFile != null ? virtualFile.getParent() : null;
        step = stepFile != null ? stepFile.getStep() : null;
    }

    public Editor getEditor() {
        return editor;
    }

    public StepFile getStepFile() {
        return stepFile;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public Step getStep() {
        return step;
    }

    public VirtualFile getStepDir() {
        return stepDir;
    }

    public boolean isValid() {
        return studyEditor != null && editor != null &&
                stepFile != null && virtualFile != null &&
                step != null && stepDir != null;
    }
}
