package org.stepik.plugin;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.StudyBasePluginConfigurator;
import org.stepik.plugin.actions.navigation.StepikNextStepAction;
import org.stepik.plugin.actions.navigation.StepikPreviousStepAction;
import org.stepik.plugin.actions.step.DownloadSubmission;
import org.stepik.plugin.actions.step.InsertStepikDirectives;
import org.stepik.plugin.actions.step.OpenInBrowserAction;
import org.stepik.plugin.actions.step.StepikResetStepAction;
import org.stepik.plugin.actions.step.StepikSendAction;
import org.stepik.plugin.actions.step.TestSamplesAction;

public class StepikPluginConfigurator extends StudyBasePluginConfigurator {
    @NotNull
    @Override
    public DefaultActionGroup getActionGroup(Project project) {
        final DefaultActionGroup group = new DefaultActionGroup();

        group.add(new StepikSendAction());
        group.add(new TestSamplesAction());
        group.add(new StepikPreviousStepAction());
        group.add(new StepikNextStepAction());
        group.add(new StepikResetStepAction());
        group.add(new DownloadSubmission());
        group.add(new InsertStepikDirectives());
        group.add(new OpenInBrowserAction());

        return group;
    }

    @Override
    public boolean accept(@NotNull Project project) {
        return StepikProjectManager.isStepikProject(project);
    }
}
