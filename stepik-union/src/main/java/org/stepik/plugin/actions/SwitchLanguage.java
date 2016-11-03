package org.stepik.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
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
import com.jetbrains.tmp.learning.LangSetting;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.plugin.collective.SupportedLanguages;

import javax.swing.*;

public class SwitchLanguage extends StudyActionWithShortcut {
    private static final String ACTION_ID = "STEPIK.SwitchLanguage";
    private static final String SHORTCUT = "ctrl alt pressed PAGE_UP";

    public SwitchLanguage() {
        super("Switch language(" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Switch language", AllIcons.Actions.Diff);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        switchLang(e.getProject());
    }

    private void switchLang(@Nullable Project project) {
        if (project == null) {
            return;
        }
        StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
        StudyState studyState = new StudyState(studyEditor);
        if (!studyState.isValid()) {
            return;
        }
        Task targetTask = studyState.getTask();
        if (targetTask == null) {
            return;
        }

        StudyTaskManager taskManager = StudyTaskManager.getInstance(project);
        LangSetting langSetting = taskManager.getLangManager().getLangSetting(targetTask);
        if (langSetting.getSupportLangs().size() == 1) {
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

        VirtualFile src = studyState.getTaskDir();
        VirtualFile hideVF = src.findChild(EduNames.HIDE);
        if (hideVF == null) {
            return;
        }
        PsiDirectory hide = PsiManager.getInstance(project).findDirectory(hideVF);
        if (hide == null) {
            return;
        }
        PsiDirectory scrPsi = PsiManager.getInstance(project).findDirectory(src);
        if (scrPsi == null) {
            return;
        }
        SupportedLanguages currentLang = SupportedLanguages.langOf(langSetting.getCurrentLang());
        if (currentLang == null) {
            return;
        }
        SupportedLanguages secondLang;

        if (currentLang == SupportedLanguages.JAVA) {
            secondLang = SupportedLanguages.PYTHON;
        } else {
            secondLang = SupportedLanguages.JAVA;
        }

        VirtualFile firstVF = src.findChild(currentLang.getMainFileName());
        if (firstVF == null) {
            return;
        }
        final PsiFile first = PsiManager
                .getInstance(project)
                .findFile(firstVF);
        if (first == null) {
            return;
        }

        VirtualFile secondVF = hide.getVirtualFile().findChild(secondLang.getMainFileName());
        if (secondVF == null) {
            return;
        }
        final PsiFile second = PsiManager
                .getInstance(project)
                .findFile(secondVF);
        if (second == null) {
            return;
        }

        ApplicationManager.getApplication().runWriteAction(() -> {
            MoveFilesOrDirectoriesUtil.doMoveFile(first, hide);
            MoveFilesOrDirectoriesUtil.doMoveFile(second, scrPsi);
        });
        String activateFileName = secondLang.getMainFileName();
        langSetting.setCurrentLang(secondLang.getName());

        VirtualFile vf = src.findChild(activateFileName);
        if (vf != null)
            FileEditorManager.getInstance(project).openFile(vf, true);
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
