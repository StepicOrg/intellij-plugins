package org.stepik.from.edu.intellij.utils.generation;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
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
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class EduUtilModuleBuilder extends JavaModuleBuilder {

    public EduUtilModuleBuilder(String moduleDir) {
        setName(EduNames.UTIL);
        setModuleFilePath(FileUtil.join(moduleDir,
                EduNames.UTIL,
                EduNames.UTIL + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module baseModule = super.createModule(moduleModel);
        String directory = getModuleFileDirectory();
        if (directory == null) {
            return baseModule;
        }
        VirtualFile moduleDir = VfsUtil.findFileByIoFile(new File(directory), true);
        if (moduleDir == null) {
            return baseModule;
        }
        VirtualFile src = moduleDir.findChild("src");
        if (src == null) {
            return baseModule;
        }
        Project project = baseModule.getProject();
        Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null) {
            return baseModule;
        }
        String courseDirectory = course.getCourseDirectory();
        FileUtil.copyDirContent(new File(courseDirectory, EduNames.UTIL), new File(src.getPath()));
        return baseModule;
    }
}
