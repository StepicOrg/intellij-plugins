
package org.stepik.plugin.java.project.wizard;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.core.EduNames;
import org.stepik.from.edu.intellij.utils.generation.builders.LessonBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class StepikJavaSectionBuilder extends JavaModuleBuilder implements LessonBuilder {
    private static final Logger LOG = Logger.getInstance(StepikJavaLessonBuilder.class);
    private final Module myUtilModule;
    private List<Pair<String, String>> mySourcePaths;

    public StepikJavaSectionBuilder(@NotNull String moduleDir,int sectionIndex, @NotNull Module utilModule) {
        myUtilModule = utilModule;
        String sectionName = EduNames.SECTION + sectionIndex;
        setName(sectionName);
        setModuleFilePath(FileUtil.join(moduleDir, sectionName, sectionName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @Override
    public Module createLesson(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        return createModule(moduleModel);
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module baseModule = super.createModule(moduleModel);
        return baseModule;
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        return mySourcePaths;
    }
}
