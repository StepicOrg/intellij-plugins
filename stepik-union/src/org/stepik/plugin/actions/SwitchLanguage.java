package org.stepik.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
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
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SwitchLanguage extends StudyActionWithShortcut {
    public static final String ACTION_ID = "STEPIK.SwitchLanguage";
    public static final String SHORTCUT = "ctrl pressed PAGE_UP";

    public SwitchLanguage() {
        super("Switch language(" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")", "Switch language", AllIcons.Actions.Diff);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        switchLang(e.getProject());
    }

    private void switchLang(Project project) {
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
            documentManager.saveDocument(documentManager.getDocument(file));
            editorManager.closeFile(file);
        }

        VirtualFile src = studyState.getTaskDir();
        PsiDirectory hide = PsiManager.getInstance(project).findDirectory(src.findChild("hide"));
        PsiDirectory scrPsi = PsiManager.getInstance(project).findDirectory(src);

        PsiFile java;
        PsiFile python;

        String activateFileName = null;
        switch (langSetting.getCurrentLang()) {
            case ("java8"):

                java = PsiManager.getInstance(project).findFile(src.findChild("Main.java"));
                python = PsiManager.getInstance(project).findFile(hide.getVirtualFile().findChild("main.py"));
                ApplicationManager.getApplication().runWriteAction(() -> {
                    MoveFilesOrDirectoriesUtil.doMoveFile(java, hide);
                    MoveFilesOrDirectoriesUtil.doMoveFile(python, scrPsi);
                });
                activateFileName = "main.py";
//                try {
//                    Files.move(Paths.get(FileUtil.join(src.getPath(), "Main.java")), Paths.get(FileUtil.join(src.getPath(), "hide", "Main.java")), StandardCopyOption.REPLACE_EXISTING);
//                    Files.move(Paths.get(FileUtil.join(src.getPath(), "hide", "main.py")), Paths.get(src.getPath(), "main.py"), StandardCopyOption.REPLACE_EXISTING);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                langSettings.currentLang =  "python3";
                langSetting.setCurrentLang("python3");
                break;
            case ("python3"):
                python = PsiManager.getInstance(project).findFile(src.findChild("main.py"));
                java = PsiManager.getInstance(project).findFile(hide.getVirtualFile().findChild("Main.java"));
                ApplicationManager.getApplication().runWriteAction(() -> {
                    MoveFilesOrDirectoriesUtil.doMoveFile(python, hide);
                    MoveFilesOrDirectoriesUtil.doMoveFile(java, scrPsi);
                });
                activateFileName = "Main.java";
//                try {
//                    Files.move(Paths.get(FileUtil.join(src.getPath(), "hide", "Main.java")), Paths.get(FileUtil.join(src.getPath(), "Main.java")), StandardCopyOption.REPLACE_EXISTING);
//                    Files.move(Paths.get(FileUtil.join(src.getPath(), "main.py")), Paths.get(src.getPath(), "hide", "main.py"), StandardCopyOption.REPLACE_EXISTING);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                langSettings.currentLang = "java8";
                langSetting.setCurrentLang("java8");
                break;
        }
        if (activateFileName == null) return;

//        LocalFileSystem.getInstance().refresh(false);
//        LocalFileSystem.getInstance().refresh(false);
        VirtualFile vf = src.findChild(activateFileName);
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
