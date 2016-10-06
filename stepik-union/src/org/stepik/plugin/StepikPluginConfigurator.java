package org.stepik.plugin;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StudyBasePluginConfigurator;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.actions.StudyRefreshTaskFileAction;
import com.jetbrains.tmp.learning.courseFormat.Course;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.actions.*;

public class StepikPluginConfigurator extends StudyBasePluginConfigurator {
    @NotNull
    @Override
    public DefaultActionGroup getActionGroup(Project project) {
//        DefaultActionGroup baseGroup = super.getActionGroup(project);
        final DefaultActionGroup group = new DefaultActionGroup();

        group.add(new StepikJavaPostAction());
        group.add(new StepikPreviousTaskAction());
        group.add(new StepikNextTaskAction());
//        resetTaskFile.getTemplatePresentation().setIcon(EduKotlinIcons.RESET_TASK_FILE);
        group.add(new StudyRefreshTaskFileAction());
        group.add(new DownloadSubmission());
        group.add(new SwitchLanguage());
        group.add(new InsertStepikDirectives());
//        StudyFillPlaceholdersAction fillPlaceholdersAction = new StudyFillPlaceholdersAction();
//        fillPlaceholdersAction.getTemplatePresentation().setIcon(EduKotlinIcons.FILL_PLACEHOLDERS_ICON);
//        fillPlaceholdersAction.getTemplatePresentation().setText("Fill Answer Placeholders");
//        group.add(fillPlaceholdersAction);
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
        if (instance == null) return false;
        Course course = instance.getCourse();
//        return builders != null && "PyCharm".equals(builders.getCourseType()) && "JAVA".equals(builders.getLanguage());
        return course != null;
    }

    @NotNull
    @Override
    public String getLanguageScriptUrl() {
        return getClass().getResource("/code_mirror/clike.js").toExternalForm();
    }
}
