package org.stepik.core.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

/**
 * @author meanmail
 */
public class Utils {
    public static Project getCurrentProject() {
        ProjectManager projectManger = ProjectManager.getInstance();
        if (projectManger.getOpenProjects().length == 0) {
            return projectManger.getDefaultProject();
        } else {
            return projectManger.getOpenProjects()[0];
        }
    }
}
