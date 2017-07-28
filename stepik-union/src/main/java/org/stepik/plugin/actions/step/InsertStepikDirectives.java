package org.stepik.plugin.actions.step;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.utils.Utils;
import org.stepik.plugin.actions.ActionUtils;
import org.stepik.plugin.utils.ReformatUtils;

import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.utils.DirectivesUtilsKt.containsDirectives;
import static org.stepik.core.utils.DirectivesUtilsKt.getFileText;
import static org.stepik.core.utils.DirectivesUtilsKt.insertAmbientCode;
import static org.stepik.core.utils.DirectivesUtilsKt.removeAmbientCode;
import static org.stepik.core.utils.DirectivesUtilsKt.writeInToFile;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;


public class InsertStepikDirectives extends CodeQuizAction {
    private static final String SHORTCUT = "ctrl alt pressed R";
    private static final String ACTION_ID = "STEPIK.InsertStepikDirectives";
    private static final String SHORTCUT_TEXT = ActionUtils.getShortcutText(SHORTCUT);
    private static final String TEXT = "Repair standard template (" + SHORTCUT_TEXT + ")";
    private static final String DESCRIPTION = "Insert Stepik directives. Repair ordinary template if it is possible.";

    public InsertStepikDirectives() {
        super(TEXT, DESCRIPTION, AllIcons.General.ExternalToolsSmall);
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
        StepNode stepNode = getCurrentCodeStepNode(project);
        if (stepNode == null) {
            return;
        }

        Utils.INSTANCE.saveAllDocuments(project);

        SupportedLanguages currentLang = stepNode.getCurrentLang();

        VirtualFile src = getOrCreateSrcDirectory(project, stepNode, true);
        if (src == null) {
            return;
        }

        VirtualFile file = src.findChild(currentLang.getMainFileName());
        if (file == null) {
            return;
        }

        String text = getFileText(file);

        StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
        boolean showHint = projectManager != null && projectManager.getShowHint();
        boolean needInsert = !containsDirectives(text, currentLang);
        if (needInsert) {
            text = insertAmbientCode(text, currentLang, showHint);
            Metrics.insertAmbientCodeAction(project, stepNode, SUCCESSFUL);
        } else {
            text = removeAmbientCode(text, showHint, currentLang, true);
            Metrics.removeAmbientCodeAction(project, stepNode, SUCCESSFUL);
        }
        writeInToFile(text, file, project);
        if (needInsert) {
            final Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                ReformatUtils.reformatSelectedEditor(project, document);
            }
        }

        currentLang.getRunner().updateRunConfiguration(project, stepNode);
    }
}
