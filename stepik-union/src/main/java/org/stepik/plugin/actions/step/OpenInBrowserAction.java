package org.stepik.plugin.actions.step;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
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

abstract class OpenInBrowserAction extends AbstractStepAction {
    private static final String ACTION_ID = "STEPIK.OpenInBrowser";
    private static final String SHORTCUT = "ctrl shift pressed HOME";

    OpenInBrowserAction() {
        super("View this step on Stepik (" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "View this step on Stepik", AllStepikIcons.stepikLogo);
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
        if (project == null) {
            return;
        }

        StudyNode<?, ?> studyNode = StepikProjectManager.getSelected(project);
        if (!(studyNode instanceof StepNode)) {
            return;
        }

        StepNode stepNode = (StepNode) studyNode;

        StudyNode parent = stepNode.getParent();
        String link = Urls.STEPIK_URL;
        if (parent != null) {
            link = String.format("%s/lesson/%d/step/%d", link, parent.getId(), stepNode.getPosition());
        }

        BrowserUtil.browse(link);
    }
}
