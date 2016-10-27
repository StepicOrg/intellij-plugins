package org.stepik.plugin;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StudyBasePluginConfigurator;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.courseFormat.Course;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.actions.DownloadSubmission;
import org.stepik.plugin.actions.InsertStepikDirectives;
import org.stepik.plugin.actions.StepikJavaPostAction;
import org.stepik.plugin.actions.StepikNextTaskAction;
import org.stepik.plugin.actions.StepikPreviousTaskAction;
import org.stepik.plugin.actions.StepikRefreshTaskFileAction;
import org.stepik.plugin.actions.SwitchLanguage;

public class StepikPluginConfigurator extends StudyBasePluginConfigurator {
    @NotNull
    @Override
    public DefaultActionGroup getActionGroup(Project project) {
        final DefaultActionGroup group = new DefaultActionGroup();

        group.add(new StepikJavaPostAction());
        group.add(new StepikPreviousTaskAction());
        group.add(new StepikNextTaskAction());
        group.add(new StepikRefreshTaskFileAction());
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
        StudyTaskManager instance = StudyTaskManager.getInstance(project);
        if (instance == null)
            return false;
        Course course = instance.getCourse();

        return course != null;
    }

    @NotNull
    @Override
    public String getLanguageScriptUrl() {
        return getClass().getResource("/code_mirror/clike.js").toExternalForm();
    }
}
