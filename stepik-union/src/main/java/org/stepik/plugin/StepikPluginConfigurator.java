package org.stepik.plugin;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyBasePluginConfigurator;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.actions.DownloadSubmission;
import org.stepik.plugin.actions.InsertStepikDirectives;
import org.stepik.plugin.actions.StepikJavaPostAction;
import org.stepik.plugin.actions.StepikNextStepAction;
import org.stepik.plugin.actions.StepikPreviousStepAction;
import org.stepik.plugin.actions.StepikResetStepAction;
import org.stepik.plugin.actions.SwitchLanguage;

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
        group.add(new SwitchLanguage());
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
        StepikProjectManager stepikProjectManager = StepikProjectManager.getInstance(project);
        if (stepikProjectManager == null)
            return false;
        CourseNode courseNode = stepikProjectManager.getCourseNode();

        return courseNode != null;
    }

    @NotNull
    @Override
    public String getLanguageScriptUrl() {
        return getClass().getResource("/code_mirror/clike.js").toExternalForm();
    }
}
