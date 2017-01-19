package com.jetbrains.tmp.learning;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import org.jetbrains.annotations.Nullable;

public class StudyState {
    private final StudyEditor studyEditor;
    private final Editor editor;
    private final StepFile stepFile;
    private final VirtualFile virtualFile;
    private final StepNode stepNode;
    private final VirtualFile stepDir;

    public StudyState(@Nullable final StudyEditor studyEditor) {
        this.studyEditor = studyEditor;
        editor = studyEditor != null ? studyEditor.getEditor() : null;
        stepFile = studyEditor != null ? studyEditor.getStepFile() : null;
        virtualFile = editor != null ? FileDocumentManager.getInstance().getFile(editor.getDocument()) : null;
        stepDir = virtualFile != null ? virtualFile.getParent() : null;
        stepNode = stepFile != null ? stepFile.getStepNode() : null;
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

    public StepNode getStepNode() {
        return stepNode;
    }

    public VirtualFile getStepDir() {
        return stepDir;
    }

    public boolean isValid() {
        return studyEditor != null && editor != null &&
                stepFile != null && virtualFile != null &&
                stepNode != null && stepDir != null;
    }
}
