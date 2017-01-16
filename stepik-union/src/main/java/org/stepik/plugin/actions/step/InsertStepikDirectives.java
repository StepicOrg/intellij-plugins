package org.stepik.plugin.actions.step;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.plugin.utils.DirectivesUtils;
import org.stepik.plugin.utils.ReformatUtils;

import javax.swing.*;

import static org.stepik.plugin.utils.DirectivesUtils.insertAmbientCode;
import static org.stepik.plugin.utils.DirectivesUtils.removeAmbientCode;
import static org.stepik.plugin.utils.DirectivesUtils.writeInToFile;


public class InsertStepikDirectives extends AbstractStepAction {
    private static final String SHORTCUT = "ctrl alt pressed R";
    private static final String ACTION_ID = "STEPIK.InsertStepikDirectives";

    public InsertStepikDirectives() {
        super("Repair standard template(" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Insert Stepik directives. Repair ordinary template if it is possible.",
                AllIcons.General.ExternalToolsSmall);
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

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;
        StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
        StudyState studyState = new StudyState(studyEditor);
        if (!studyState.isValid()) {
            return;
        }
        StepNode targetStepNode = studyState.getStepNode();
        if (targetStepNode == null) {
            return;
        }

        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            Document document = documentManager.getDocument(file);
            if (document != null)
                documentManager.saveDocument(document);
        }

        SupportedLanguages currentLang = targetStepNode.getCurrentLang();
        VirtualFile src = studyState.getStepDir();
        VirtualFile file = src.findChild(currentLang.getMainFileName());
        if (file == null) {
            return;
        }
        String[] text = DirectivesUtils.getFileText(file);

        Pair<Integer, Integer> locations = DirectivesUtils.findDirectives(text, currentLang);
        boolean showHint = StepikProjectManager.getInstance(project).getShowHint();
        boolean needInsert = locations.first == -1 && locations.second == text.length;
        if (needInsert) {
            text = insertAmbientCode(text, currentLang, showHint);
        } else {
            text = removeAmbientCode(text, locations, project, showHint, currentLang);
        }
        writeInToFile(text, file, project);
        if (needInsert) {
            ReformatUtils.reformatSelectedEditor(project);
        }
    }
}