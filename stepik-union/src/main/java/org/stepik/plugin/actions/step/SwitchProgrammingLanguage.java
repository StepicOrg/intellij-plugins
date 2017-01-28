package org.stepik.plugin.actions.step;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesUtil;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

import static com.jetbrains.tmp.learning.SupportedLanguages.JAVA;
import static com.jetbrains.tmp.learning.SupportedLanguages.PYTHON;

public class SwitchProgrammingLanguage extends AbstractStepAction {
    private static final Logger logger = Logger.getInstance(SwitchProgrammingLanguage.class);
    private static final String ACTION_ID = "STEPIK.SwitchProgrammingLanguage";
    private static final String SHORTCUT = "ctrl alt pressed PAGE_UP";

    public SwitchProgrammingLanguage() {
        super("Switch programming language(" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Switch programming language", AllIcons.Actions.Diff);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        StepNode targetStepNode = StudyUtils.getSelectedStep(project);
        if (targetStepNode == null) {
            return;
        }

        switchLang(project, targetStepNode);
    }

    private void switchLang(@NotNull Project project, @NotNull StepNode targetStepNode) {
        if (targetStepNode.getSupportedLanguages().size() == 1) {
            return;
        }

        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            Document document = documentManager.getDocument(file);
            if (document == null)
                continue;
            documentManager.saveDocument(document);
            editorManager.closeFile(file);
        }

        VirtualFile srcParent = project.getBaseDir().findFileByRelativePath(targetStepNode.getPath());
        if (srcParent == null) {
            return;
        }

        PsiDirectory src = getOrCreateDir(project, srcParent, EduNames.SRC);
        if (src == null) {
            return;
        }

        PsiDirectory hide = getOrCreateDir(project, src.getVirtualFile(), EduNames.HIDE);
        if (hide == null) {
            return;
        }

        SupportedLanguages currentLang = targetStepNode.getCurrentLang();
        SupportedLanguages secondLang = currentLang == JAVA ? PYTHON : JAVA;

        PsiFile second = getOrCreateMainFile(project, hide.getVirtualFile(), secondLang, targetStepNode);
        if (second == null) {
            logger.error("Can't create Main file: " + secondLang.getMainFileName());
            return;
        }

        final PsiFile first = src.findFile(currentLang.getMainFileName());
        ApplicationManager.getApplication().runWriteAction(() -> {
            if (first != null) {
                MoveFilesOrDirectoriesUtil.doMoveFile(first, hide);
            }
            MoveFilesOrDirectoriesUtil.doMoveFile(second, src);
        });

        targetStepNode.setCurrentLang(secondLang);
        FileEditorManager.getInstance(project).openFile(second.getVirtualFile(), true);
    }

    private PsiDirectory getOrCreateDir(
            @NotNull Project project,
            @NotNull VirtualFile parent,
            @NotNull String name) {
        final VirtualFile[] hideVF = {parent.findChild(name)};

        if (hideVF[0] == null) {
            ApplicationManager
                    .getApplication()
                    .runWriteAction(() -> {
                        try {
                            hideVF[0] = parent.createChildDirectory(this, name);
                        } catch (IOException e) {
                            hideVF[0] = null;
                        }
                    });
            if (hideVF[0] == null) {
                return null;
            }
        }

        return PsiManager.getInstance(project).findDirectory(hideVF[0]);
    }

    @Nullable
    private PsiFile getOrCreateMainFile(
            @NotNull Project project,
            @NotNull VirtualFile parent,
            @NotNull SupportedLanguages language,
            @NotNull StepNode stepNode) {
        String fileName = language.getMainFileName();
        final VirtualFile[] file = {parent.findChild(fileName)};

        if (file[0] == null) {
            ApplicationManager
                    .getApplication()
                    .runWriteAction(() -> {
                        try {
                            file[0] = parent.createChildData(this, fileName);

                            StepFile stepFile = stepNode.getStepFiles().get(fileName);
                            if (stepFile != null) {
                                file[0].setBinaryContent(stepFile.getText().getBytes());
                            }
                        } catch (IOException e) {
                            file[0] = null;
                        }
                    });
        }
        return PsiManager.getInstance(project).findFile(file[0]);
    }

    @NotNull
    @Override
    public String getActionId() {
        return ACTION_ID;
    }

    @Nullable
    @Override
    public String[] getShortcuts() {
        return new String[]{SHORTCUT};
    }
}
