package com.jetbrains.tmp.learning.checker;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.TaskInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.jetbrains.tmp.learning.StudyUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class StudyCheckUtils {
    private StudyCheckUtils() {
    }

    public static void showTestResultPopUp(final String text, Color color, @NotNull final Project project) {
        BalloonBuilder balloonBuilder =
                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, null, color, null);
        final Balloon balloon = balloonBuilder.createBalloon();
        StudyUtils.showCheckPopUp(project, balloon);
    }

    public static boolean hasBackgroundProcesses(@NotNull Project project) {
        final IdeFrame frame = ((WindowManagerEx) WindowManager.getInstance()).findFrameFor(project);
        final StatusBarEx statusBar = frame == null ? null : (StatusBarEx) frame.getStatusBar();
        if (statusBar != null) {
            final List<Pair<TaskInfo, ProgressIndicator>> processes = statusBar.getBackgroundProcesses();
            if (!processes.isEmpty()) return true;
        }
        return false;
    }
}