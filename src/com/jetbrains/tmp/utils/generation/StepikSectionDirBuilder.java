package com.jetbrains.tmp.utils.generation;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class StepikSectionDirBuilder extends JavaModuleBuilder {
    private static final Logger LOG = Logger.getInstance(StepikSectionDirBuilder.class);
    String sectionDir;
    String lessonDir;
    String sectionName;
    String lessonName;

    public StepikSectionDirBuilder(String moduleDir, Lesson lesson) {
        String[] dirs = lesson.getName().split("#/\\*");
//        -------------
        lesson.setName(dirs[1]);
//        -------------
        sectionName = dirs[0];
        lessonName = dirs[1];
        sectionDir = moduleDir + "/" + sectionName;
        lessonDir = sectionDir + "/" + lessonName;
    }

    public void build(){
        File file = new File(sectionDir);
        if (!file.exists()){
            if (!file.mkdirs()) {
                LOG.warn("section dir was not created");
            } else {
                LOG.info(file.getAbsolutePath());
            }
        }
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        return super.createModule(moduleModel);
    }

    public String getLessonDir() {
        return lessonDir;
    }

    public String getSectionDir(){
        return sectionDir;
    }

    public String getSectionName(){
        return sectionName;
    }
}
