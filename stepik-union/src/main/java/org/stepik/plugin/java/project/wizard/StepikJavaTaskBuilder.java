package org.stepik.plugin.java.project.wizard;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.LangSetting;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Task;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.from.edu.intellij.utils.generation.builders.TaskBuilder;
import org.stepik.plugin.collective.SupportedLanguages;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StepikJavaTaskBuilder extends JavaModuleBuilder implements TaskBuilder {
    private static final Logger logger = Logger.getInstance(StepikJavaTaskBuilder.class);
    private final Task myTask;
    private final Module myUtilModule;
    private static final String SRC = "src";

    public StepikJavaTaskBuilder(
            String moduleDir, @NotNull String name, @NotNull Task task,
            @NotNull Module utilModule) {
        myTask = task;
        myUtilModule = utilModule;
        String taskName = EduNames.TASK + task.getIndex();
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
            return module;
        }

        ModuleRootModificationUtil.addDependency(module, myUtilModule);
        return module;
    }

    private boolean createTaskContent() throws IOException {
        StudyTaskManager taskManager = StudyTaskManager.getInstance(myUtilModule.getProject());
        SupportedLanguages defaultLang = SupportedLanguages.langOf(taskManager.getDefaultLang());
        Course course = myTask.getLesson().getSection().getCourse();
        String directory = getModuleFileDirectory();
        if (directory == null) {
            return false;
        }
        VirtualFile moduleDir = VfsUtil.findFileByIoFile(new File(directory), true);
        if (moduleDir == null) {
            return false;
        }
        VirtualFile src = moduleDir.findChild(SRC);
        if (src == null) {
            return false;
        }
        String courseResourcesDirectory = course.getCourseDirectory();
        String taskResourcesPath = FileUtil.join(courseResourcesDirectory,
                EduNames.LESSON + myTask.getLesson().getIndex(), EduNames.TASK + myTask.getIndex());
        FileUtil.copyDirContent(new File(taskResourcesPath), new File(FileUtil.join(src.getPath(), EduNames.HIDE)));

        Set<SupportedLanguages> supportedLang = getSupportedLang(taskResourcesPath);
        SupportedLanguages currentLang;
        if (supportedLang.contains(defaultLang)) {
            currentLang = defaultLang;
        } else {
            currentLang = getPopularLang(supportedLang);
        }

        taskManager.getLangManager().setLangSetting(myTask,
                new LangSetting(currentLang != null ? currentLang.getName() : null,
                        supportedLang.stream()
                                .map(SupportedLanguages::getName)
                                .collect(Collectors.toSet())));
        if (currentLang != null)
            moveFromHide(currentLang.getMainFileName(), src);
        moveFromHide("task.html", src);

        return true;
    }

    @Nullable
    private SupportedLanguages getPopularLang(@NotNull Set<SupportedLanguages> supportedLang) {
        for (SupportedLanguages lang : SupportedLanguages.values())
            if (supportedLang.contains(lang))
                return lang;
        return null;
    }

    private void moveFromHide(@NotNull String filename, @NotNull VirtualFile src) throws IOException {
        Files.move(Paths.get(FileUtil.join(src.getPath(), EduNames.HIDE, filename)),
                Paths.get(src.getPath(), filename), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Module createTask(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException,
            IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        return createModule(moduleModel);
    }

    @NotNull
    private Set<SupportedLanguages> getSupportedLang(@NotNull String path) {
        Set<SupportedLanguages> supportedLang = new HashSet<>();
        Path dir = Paths.get(path);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.java")) {
            if (stream.iterator().hasNext()) {
                supportedLang.add(SupportedLanguages.JAVA);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.py")) {
            if (stream.iterator().hasNext()) {
                supportedLang.add(SupportedLanguages.PYTHON);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return supportedLang;
    }
}
