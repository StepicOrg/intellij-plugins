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
import core.com.jetbrains.tmp.learning.LangSetting;
import core.com.jetbrains.tmp.learning.StudyTaskManager;
import core.com.jetbrains.tmp.learning.core.EduNames;
import core.com.jetbrains.tmp.learning.courseFormat.Course;
import core.com.jetbrains.tmp.learning.courseFormat.Task;
import org.stepik.from.edu.intellij.utils.EduIntelliJNames;
import org.stepik.from.edu.intellij.utils.generation.builders.TaskBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class StepikJavaTaskBuilder extends JavaModuleBuilder implements TaskBuilder {
    private static final Logger LOG = Logger.getInstance(StepikJavaTaskBuilder.class);
    private final Task myTask;
    private final Module myUtilModule;

    public StepikJavaTaskBuilder(String moduleDir, @NotNull String name, @NotNull Task task, @NotNull Module utilModule) {
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
//        addJUnitLib(module);
        ModuleRootModificationUtil.addDependency(module, myUtilModule);
        return module;
    }

//    private void addJUnitLib(Module module) {
//        ExternalLibraryDescriptor descriptor = JUnitExternalLibraryDescriptor.JUNIT4;
//        List<String> defaultRoots = descriptor.getLibraryClassesRoots();
//        final List<String> urls = OrderEntryFix.refreshAndConvertToUrls(defaultRoots);
//        ModuleRootModificationUtil.addModuleLibrary(module, descriptor.getPresentableName(), urls, Collections.emptyList());
//    }

    private boolean createTaskContent() throws IOException {
        StudyTaskManager taskManager = StudyTaskManager.getInstance(myUtilModule.getProject());
        String defaultLang = taskManager.getDefaultLang();
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
        FileUtil.copyDirContent(new File(taskResourcesPath), new File(FileUtil.join(src.getPath(), "hide")));

        Set<String> supportedLang = getSupportedLang(taskResourcesPath);
        String currentLang = null;
        if (supportedLang.contains(defaultLang)){
            currentLang = defaultLang;
        }
        else {
            currentLang = getPopularLang(supportedLang);
        }

//        taskManager.putCurrentLang(myTask, currentLang);
//        taskManager.putSupportedLang(myTask, supportedLang);

        taskManager.getLangManager().setLangSetting(myTask, new LangSetting(currentLang, supportedLang));

//        if (!supportedLang.contains(currentLang)) {
//            currentLang = currentLang.equals("java8") ? "python3" : "java8";
//        }
        moveFromHide(currentLang, src);
        moveFromHide("task.html", src);

        return true;
    }

    private String getPopularLang(Set<String> supportedLang) {
        if (supportedLang.contains("python3"))
            return "python3";
        if (supportedLang.contains("java8"))
            return "java8";
        return "empty_lang";
    }

    private void moveFromHide(String currentLang, VirtualFile src) throws IOException {
        String filename = "task.html";
        switch (currentLang) {
            case ("java8"):
                filename = "Main.java";
                break;
            case ("python3"):
                filename = "main.py";
                break;
        }
        Files.move(Paths.get(FileUtil.join(src.getPath(), "hide", filename)), Paths.get(src.getPath(), filename), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Module createTask(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        return createModule(moduleModel);
    }

    public Set<String> getSupportedLang(String path) {
        Set<String> supportedLang = new HashSet<>();
        Path dir = Paths.get(path);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.java")) {
            for (Path file : stream) {
                supportedLang.add("java8");
                break;
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.py")) {
            for (Path file : stream) {
                supportedLang.add("python3");
                break;
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
        return supportedLang;
    }
}
