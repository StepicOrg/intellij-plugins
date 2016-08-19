package org.stepic.plugin.java.project.wizard;

import com.intellij.codeInsight.daemon.impl.quickfix.OrderEntryFix;
import com.intellij.execution.junit.JUnitExternalLibraryDescriptor;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ExternalLibraryDescriptor;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.core.EduNames;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.courseFormat.Task;
import com.jetbrains.edu.utils.EduIntelliJNames;
import com.jetbrains.edu.utils.generation.builders.TaskBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

public class StepicJavaTaskBuilder extends JavaModuleBuilder implements TaskBuilder {
    private static final Logger LOG = Logger.getInstance(StepicJavaTaskBuilder.class);
    private final Task myTask;
    private final Module myUtilModule;

    public StepicJavaTaskBuilder(String moduleDir, @NotNull String name, @NotNull Task task, @NotNull Module utilModule) {
        myTask = task;
        myUtilModule = utilModule;
        String taskName = EduNames.TASK + task.getIndex();
        //module name like lessoni-taski
        String moduleName = name + "-" + taskName;
        setName(moduleName);
        setModuleFilePath(FileUtil.join(moduleDir, taskName, moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }


    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module module = super.createModule(moduleModel);
        if (!createTaskContent()) {
            LOG.info("Failed to copy task content");
            return module;
        }
        addJUnitLib(module);
        ModuleRootModificationUtil.addDependency(module, myUtilModule);
        return module;
    }

    private void addJUnitLib(Module module) {
        ExternalLibraryDescriptor descriptor = JUnitExternalLibraryDescriptor.JUNIT4;
        List<String> defaultRoots = descriptor.getLibraryClassesRoots();
        final List<String> urls = OrderEntryFix.refreshAndConvertToUrls(defaultRoots);
        ModuleRootModificationUtil.addModuleLibrary(module, descriptor.getPresentableName(), urls, Collections.emptyList());
    }

    private boolean createTaskContent() throws IOException {
        Course course = myTask.getLesson().getCourse();
        String directory = getModuleFileDirectory();
        if (directory == null) {
            return false;
        }
        VirtualFile moduleDir = VfsUtil.findFileByIoFile(new File(directory), true);
        if (moduleDir == null) {
            return false;
        }
        VirtualFile src = moduleDir.findChild(EduIntelliJNames.SRC);
        if (src == null) {
            return false;
        }
        String courseResourcesDirectory = course.getCourseDirectory();
        String taskResourcesPath = FileUtil.join(courseResourcesDirectory, EduNames.LESSON + myTask.getLesson().getIndex(),
                EduNames.TASK + myTask.getIndex());
//        FileUtil.copyDirContent(new File(taskResourcesPath), new File(src.getPath()));
        FileUtil.copyDirContent(new File(taskResourcesPath), new File(FileUtil.join(src.getPath(), "hide")));
        String currentLang = StudyTaskManager.getInstance(myUtilModule.getProject()).getLang(myTask);
        switch (currentLang) {
            case ("java"):
                Files.move(Paths.get(FileUtil.join(src.getPath(), "hide", "Main.java")), Paths.get(FileUtil.join(src.getPath(), "Main.java")), StandardCopyOption.REPLACE_EXISTING);
//                FileUtil.copyFileOrDir(new File(FileUtil.join(src.getPath(), "hide", "Main.java")), new File(FileUtil.join(src.getPath(), "Main.java")));
                break;
            case ("python3"):
                Files.move(Paths.get(FileUtil.join(src.getPath(), "hide", "main.py")), Paths.get(src.getPath(), "main.py"), StandardCopyOption.REPLACE_EXISTING);
//                FileUtil.copyFileOrDir(new File(FileUtil.join(src.getPath(), "hide", "main.py")), new File(FileUtil.join(src.getPath(), "main.py")));
                break;
        }
        return true;
    }

    @Override
    public Module createTask(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        return createModule(moduleModel);
    }
}
