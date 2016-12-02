package org.stepik.plugin.java.project.wizard;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import org.apache.commons.codec.binary.Base64;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.stepik.from.edu.intellij.utils.generation.builders.TaskBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

class StepikJavaTaskBuilder extends JavaModuleBuilder implements TaskBuilder {
    private static final Logger logger = Logger.getInstance(StepikJavaTaskBuilder.class);
    private final Task myTask;
    private final Project project;

    StepikJavaTaskBuilder(String moduleDir, @NotNull String name, @NotNull Task task, @NotNull Project project) {
        myTask = task;
        this.project = project;
        String taskName = task.getDirectory();
        //module name like lessoni-taski
        String moduleName = name + "-" + taskName;
        setName(moduleName);
        setModuleFilePath(FileUtil.join(moduleDir, taskName,
                moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException,
            IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module module = super.createModule(moduleModel);
        if (!createTaskContent()) {
            logger.info("Failed to copy task content");
        }
        return module;
    }

    private boolean createTaskContent() throws IOException {
        StudyTaskManager taskManager = StudyTaskManager.getInstance(project);
        String defaultLangName = taskManager.getDefaultLang();
        if (defaultLangName == null) {
            return false;
        }
        myTask.setCurrentLang(SupportedLanguages.langOf(defaultLangName));
        Course course = myTask.getLesson().getSection().getCourse();
        String directory = getModuleFileDirectory();
        if (directory == null) {
            return false;
        }
        VirtualFile moduleDir = VfsUtil.findFileByIoFile(new File(directory), true);
        if (moduleDir == null) {
            return false;
        }
        VirtualFile src = moduleDir.findChild(EduNames.SRC);
        if (src == null) {
            return false;
        }
        String courseResourcesDirectory = course.getCourseDirectory();
        String taskResourcesPath = FileUtil.join(courseResourcesDirectory,
                myTask.getLesson().getDirectory(), myTask.getDirectory());
        FileUtil.copyDirContent(new File(taskResourcesPath), new File(FileUtil.join(src.getPath(), EduNames.HIDE)));


        createTaskFiles(myTask, src.getPath());

        SupportedLanguages currentLang = myTask.getCurrentLang();

        if (currentLang != null)
            moveFromHide(currentLang.getMainFileName(), src);
        return true;
    }

    private void createTaskFiles(Task task, String src) {
        src = src + "/" + EduNames.HIDE;
        for (Map.Entry<String, TaskFile> taskFileEntry : task.taskFiles.entrySet()) {
            final String name = taskFileEntry.getKey();
            final TaskFile taskFile = taskFileEntry.getValue();
            final File file = new File(src, name);
            FileUtil.createIfDoesntExist(file);

            try {
                if (EduUtils.isImage(taskFile.getName())) {
                    FileUtil.writeToFile(file, Base64.decodeBase64(taskFile.getText()));
                } else {
                    FileUtil.writeToFile(file, taskFile.getText());
                }
            } catch (IOException e) {
                logger.error("ERROR copying file " + name);
            }
        }
    }

    private void moveFromHide(@NotNull String filename, @NotNull VirtualFile src) throws IOException {
        Files.move(Paths.get(src.getPath(), EduNames.HIDE, filename),
                Paths.get(src.getPath(), filename), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Module createTask(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException,
            IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        return createModule(moduleModel);
    }
}
