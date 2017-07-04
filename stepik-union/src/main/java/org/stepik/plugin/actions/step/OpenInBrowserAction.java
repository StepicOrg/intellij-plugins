package org.stepik.plugin.actions.step;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.urls.Urls;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;

import javax.swing.*;

public class OpenInBrowserAction extends AbstractStepAction {
    private static final String ACTION_ID = "STEPIK.OpenInBrowser";
    private static final String SHORTCUT = "ctrl shift pressed HOME";
    private static final String DESCRIPTION = "View this step on Stepik";

    public OpenInBrowserAction() {
        super("View this step on Stepik (" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                DESCRIPTION, AllStepikIcons.stepikLogo);
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
        ApplicationManager.getApplication()
                .executeOnPooledThread(() -> openInBrowser(e.getProject()));
    }

    private void openInBrowser(@Nullable Project project) {
        StepNode stepNode = getCurrentStep(project);
        if (stepNode == null) {
            return;
        }

        String link = getLink(project, stepNode);
        BrowserUtil.browse(link);
    }

    private String getLink(@NotNull Project project, @NotNull StepNode stepNode) {
        StudyNode parent = stepNode.getParent();
        String link = Urls.STEPIK_URL;
        if (parent != null) {
            link = String.format("%s/lesson/%d/step/%d", link, parent.getId(), stepNode.getPosition());
            if (StepikProjectManager.isAdaptive(project)) {
                link += "?adaptive=true";
            }
        }
        return link;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        Project project = e.getProject();
        StepNode stepNode = getCurrentStep(project);
        if (stepNode == null) {
            presentation.setDescription(DESCRIPTION);
            return;
        }
        String link = getLink(project, stepNode);
        presentation.setDescription(link);
    }
}
