package org.stepik.plugin.java.project.wizard;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Task;
import org.stepik.from.edu.intellij.utils.generation.builders.LessonBuilder;
import org.stepik.from.edu.intellij.utils.generation.builders.TaskBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class StepikJavaLessonBuilder extends JavaModuleBuilder implements LessonBuilder {
    private static final Logger logger = Logger.getInstance(StepikJavaLessonBuilder.class);
    private final Lesson myLesson;
    private final Module myUtilModule;

    public StepikJavaLessonBuilder(@NotNull String moduleDir, @NotNull Lesson lesson, @NotNull Module utilModule) {
        myLesson = lesson;
        myUtilModule = utilModule;
        String lessonName = lesson.getDirectory();
        setName(lessonName);
        setModuleFilePath(FileUtil.join(moduleDir, lessonName, lessonName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @Override
    public Module createLesson(@NotNull ModifiableModuleModel moduleModel)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        return createModule(moduleModel);
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
        TaskBuilder taskModuleBuilder = new StepikJavaTaskBuilder(getModuleFileDirectory(),
                getName(),
                task,
                myUtilModule);
        taskModuleBuilder.createTask(moduleModel);
    }
}
