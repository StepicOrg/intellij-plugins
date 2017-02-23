package org.stepik.plugin;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyBasePluginConfigurator;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.actions.navigation.StepikNextStepAction;
import org.stepik.plugin.actions.navigation.StepikPreviousStepAction;
import org.stepik.plugin.actions.step.DownloadSubmission;
import org.stepik.plugin.actions.step.InsertStepikDirectives;
import org.stepik.plugin.actions.step.StepikJavaPostAction;
import org.stepik.plugin.actions.step.StepikResetStepAction;

public class StepikPluginConfigurator extends StudyBasePluginConfigurator {
    @NotNull
    @Override
    public DefaultActionGroup getActionGroup(Project project) {
        final DefaultActionGroup group = new DefaultActionGroup();

        group.add(new StepikJavaPostAction());
        group.add(new StepikPreviousStepAction());
        group.add(new StepikNextStepAction());
        group.add(new StepikResetStepAction());
        group.add(new DownloadSubmission());
        group.add(new InsertStepikDirectives());

        return group;
    }

    @NotNull
    @Override
    public String getDefaultHighlightingMode() {
        return "text/x-java";
    }

    @Override
    public boolean accept(@NotNull Project project) {
        return StepikProjectManager.isStepikProject(project);
    }

    @NotNull
    @Override
    public String getLanguageScriptUrl() {
        return getClass().getResource("/code_mirror/clike.js").toExternalForm();
    }
}
