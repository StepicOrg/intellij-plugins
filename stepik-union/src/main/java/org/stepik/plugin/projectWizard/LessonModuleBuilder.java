package org.stepik.plugin.projectWizard;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Task;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

class LessonModuleBuilder extends AbstractModuleBuilder {
    private final Lesson myLesson;
    private final Project project;

    LessonModuleBuilder(@NotNull String moduleDir, @NotNull Lesson lesson, @NotNull Project project) {
        myLesson = lesson;
        this.project = project;
        String lessonName = lesson.getDirectory();
        setName(lessonName);
        setModuleFilePath(FileUtil.join(moduleDir, lessonName, lessonName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module baseModule = super.createModule(moduleModel);
        List<Task> taskList = myLesson.getTaskList();
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            task.setIndex(i + 1);
            createTaskModule(moduleModel, task);
        }
        return baseModule;
    }

    private void createTaskModule(
            @NotNull ModifiableModuleModel moduleModel,
            @NotNull Task task)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        TaskModuleBuilder taskModuleBuilder = new TaskModuleBuilder(getModuleFileDirectory(), getName(), task, project);
        taskModuleBuilder.createModule(moduleModel);
    }
}
