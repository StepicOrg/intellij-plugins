package org.stepik.core.utils;

import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class Utils {
    @NotNull
    public static Project getCurrentProject() {
        ProjectManager projectManger = ProjectManager.getInstance();
        if (projectManger.getOpenProjects().length == 0) {
            return projectManger.getDefaultProject();
        } else {
            return projectManger.getOpenProjects()[0];
        }
    }

    public static boolean isCanceled() {
        try {
            ProgressManager.checkCanceled();
        } catch (ProcessCanceledException e) {
            return true;
        }
        return false;
    }
}
