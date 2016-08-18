package org.stepic.plugin.java.project.wizard;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.edu.learning.core.EduNames;
import com.jetbrains.edu.learning.courseFormat.Lesson;
import com.jetbrains.edu.learning.courseFormat.Task;
import com.jetbrains.edu.utils.generation.builders.LessonBuilder;
import com.jetbrains.edu.utils.generation.builders.TaskBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class StepicJavaLessonBuilder extends JavaModuleBuilder implements LessonBuilder {
    private static final Logger LOG = Logger.getInstance(StepicJavaLessonBuilder.class);
    private final Lesson myLesson;
    private final Module myUtilModule;

    public StepicJavaLessonBuilder(@NotNull String moduleDir, @NotNull Lesson lesson, @NotNull Module utilModule) {
        myLesson = lesson;
        myUtilModule = utilModule;
        String lessonName = EduNames.LESSON + lesson.getIndex();
        setName(lessonName);
        setModuleFilePath(FileUtil.join(moduleDir, lessonName, lessonName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module baseModule = super.createModule(moduleModel);
        List<Task> taskList = myLesson.getTaskList();
        for (int i = 0; i < taskList.size(); i++) {
            int visibleTaskIndex = i + 1;
            Task task = taskList.get(i);
            task.setIndex(visibleTaskIndex);
            createTaskModule(moduleModel, task);
        }
        return baseModule;

    }

    private void createTaskModule(@NotNull ModifiableModuleModel moduleModel, @NotNull Task task) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        TaskBuilder taskModuleBuilder = new StepicJavaTaskBuilder(getModuleFileDirectory(), getName(), task, myUtilModule);
        taskModuleBuilder.createTask(moduleModel);
    }

    @Override
    public Module createLesson(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        return createModule(moduleModel);
    }
}
