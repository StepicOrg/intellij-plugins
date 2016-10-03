package com.jetbrains.tmp.utils.generation;

import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.tmp.learning.courseFormat.Lesson;

import java.io.File;

public class StepikSectionDirBuilder {
    private static final Logger LOG = Logger.getInstance(StepikSectionDirBuilder.class);
    String sectionDir;
    String lessonDir;

    public StepikSectionDirBuilder(String moduleDir, Lesson lesson) {
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
