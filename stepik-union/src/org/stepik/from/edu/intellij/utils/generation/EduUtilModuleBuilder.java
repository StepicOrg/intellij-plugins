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
import core.com.jetbrains.tmp.learning.StudyTaskManager;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class EduUtilModuleBuilder extends JavaModuleBuilder {

    public EduUtilModuleBuilder(String moduleDir) {
        setName("util");
        setModuleFilePath(FileUtil.join(moduleDir, "util", "util" + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
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
//        StartupManager.getInstance(project).registerPostStartupActivity(() -> DumbService.getInstance(project).runWhenSmart(() -> ApplicationManager.getApplication().runWriteAction(() -> {
//            EduIntellijUtils.addTemplate(project, src, "EduTestRunner.java");
//        })));
//        ExternalLibraryDescriptor descriptor = JUnitExternalLibraryDescriptor.JUNIT4;
//        List<String> defaultRoots = descriptor.getLibraryClassesRoots();
//        final List<String> urls = OrderEntryFix.refreshAndConvertToUrls(defaultRoots);
//        ModuleRootModificationUtil.addModuleLibrary(baseModule, descriptor.getPresentableName(), urls, Collections.<String>emptyList());

        String courseDirectory = StudyTaskManager.getInstance(project).getCourse().getCourseDirectory();
        FileUtil.copyDirContent(new File(courseDirectory, "util"), new File(src.getPath()));
        return baseModule;
    }
}
