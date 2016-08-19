package org.stepic.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.edu.learning.StudyState;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.StudyUtils;
import com.jetbrains.edu.learning.actions.StudyActionWithShortcut;
import com.jetbrains.edu.learning.courseFormat.Task;
import com.jetbrains.edu.learning.editor.StudyEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SwitchLanguage extends StudyActionWithShortcut {
    private static final Logger LOG = Logger.getInstance(SwitchLanguage.class);
    public static final String ACTION_ID = "SwitchLanguage";
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

        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            documentManager.saveDocument(documentManager.getDocument(file));
            editorManager.closeFile(file);
        }

        VirtualFile src = studyState.getTaskDir();

        StudyTaskManager taskManager = StudyTaskManager.getInstance(project);
        String currentLang = taskManager.getLang(targetTask);
        String activateFileName = null;
        switch (currentLang) {
            case ("java"):
                try {
                    Files.move(Paths.get(FileUtil.join(src.getPath(), "Main.java")), Paths.get(FileUtil.join(src.getPath(), "hide", "Main.java")), StandardCopyOption.REPLACE_EXISTING);
                    Files.move(Paths.get(FileUtil.join(src.getPath(), "hide", "main.py")), Paths.get(src.getPath(), "main.py"), StandardCopyOption.REPLACE_EXISTING);
                    activateFileName = "main.py";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                taskManager.setLang(targetTask, "python3");
                break;
            case ("python3"):
                try {
                    Files.move(Paths.get(FileUtil.join(src.getPath(), "hide", "Main.java")), Paths.get(FileUtil.join(src.getPath(), "Main.java")), StandardCopyOption.REPLACE_EXISTING);
                    Files.move(Paths.get(FileUtil.join(src.getPath(), "main.py")), Paths.get(src.getPath(), "hide", "main.py"), StandardCopyOption.REPLACE_EXISTING);
                    activateFileName = "Main.java";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                taskManager.setLang(targetTask, "java");
                break;
        }
        if (activateFileName == null) return;

        LocalFileSystem.getInstance().refresh(false);
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
