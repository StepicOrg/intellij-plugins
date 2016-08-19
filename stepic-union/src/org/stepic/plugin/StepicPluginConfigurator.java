package org.stepic.plugin;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.jetbrains.edu.learning.StudyBasePluginConfigurator;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.actions.StudyRefreshTaskFileAction;
import com.jetbrains.edu.learning.courseFormat.Course;
import org.jetbrains.annotations.NotNull;
import org.stepic.plugin.actions.StepicJavaPostAction;
import org.stepic.plugin.actions.StepicNextTaskAction;
import org.stepic.plugin.actions.StepicPreviousTaskAction;
import org.stepic.plugin.actions.SwitchLanguage;

public class StepicPluginConfigurator extends StudyBasePluginConfigurator {
    @NotNull
    @Override
    public DefaultActionGroup getActionGroup(Project project) {
        DefaultActionGroup baseGroup = super.getActionGroup(project);
        final DefaultActionGroup group = new DefaultActionGroup();

        StepicJavaPostAction postAction = new StepicJavaPostAction();
        group.add(postAction);
        group.add(new StepicPreviousTaskAction());
        group.add(new StepicNextTaskAction());
//        resetTaskFile.getTemplatePresentation().setIcon(EduKotlinIcons.RESET_TASK_FILE);
        group.add(new StudyRefreshTaskFileAction());
        group.add(new SwitchLanguage());
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
