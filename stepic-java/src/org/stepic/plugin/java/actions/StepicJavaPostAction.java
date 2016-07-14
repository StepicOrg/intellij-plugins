package org.stepic.plugin.java.actions;

import com.intellij.openapi.project.Project;
import com.jetbrains.edu.learning.actions.StudyCheckAction;
import org.jetbrains.annotations.NotNull;

public class StepicJavaPostAction extends StudyCheckAction {

    @Override
    public void check(@NotNull Project project) {

    }


    @NotNull
    @Override
    public String getActionId() {
        return "StepicJavaPostAction";
    }


}
