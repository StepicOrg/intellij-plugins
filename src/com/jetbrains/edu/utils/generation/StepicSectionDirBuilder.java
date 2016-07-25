package com.jetbrains.edu.utils.generation;

import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.edu.learning.courseFormat.Lesson;

import java.io.File;

public class StepicSectionDirBuilder {
    private static final Logger LOG = Logger.getInstance(StepicSectionDirBuilder.class);
    String sectionDir;
    String lessonDir;

    public StepicSectionDirBuilder(String moduleDir, Lesson lesson) {
        String[] dirs = lesson.getName().split("#/\\*");
        lesson.setName(dirs[1]);
        sectionDir = moduleDir + "/" + dirs[0];
        lessonDir = sectionDir + "/" + dirs[1];
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

    public String getLessonDir() {
        return lessonDir;
    }

    public String getSectionDir(){
        return sectionDir;
    }
}
