package org.stepik.from.edu.intellij.utils.generation;

import com.jetbrains.tmp.learning.courseFormat.Lesson;

public class StepikSectionDirConfigurator {
    private String sectionDir;
    private String lessonDir;
    private String sectionName;
    private String lessonName;

    public StepikSectionDirConfigurator(String moduleDir, Lesson lesson) {
        String[] dirs = lesson.getName().split("#/\\*");
//        -------------
        lesson.setName(dirs[1]);
//        -------------
        sectionName = dirs[0];
        lessonName = dirs[1];
        sectionDir = moduleDir + "/" + sectionName;
        lessonDir = sectionDir + "/" + lessonName;
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
