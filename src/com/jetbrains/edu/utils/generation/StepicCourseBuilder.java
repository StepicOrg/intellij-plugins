package com.jetbrains.edu.utils.generation;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.courseFormat.Lesson;
import com.jetbrains.edu.learning.stepic.CourseInfo;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public interface StepicCourseBuilder {
    static final Logger LOG = Logger.getInstance(StepicCourseBuilder.class);

    void createCourseFromCourseInfo(@NotNull ModifiableModuleModel moduleModel, Project project, EduProjectGenerator generator, CourseInfo courseInfo) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException;

    default void createLessonModules(@NotNull ModifiableModuleModel moduleModel, Course course, String moduleDir, Module utilModule) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        List<Lesson> lessons = course.getLessons();
        for (int i = 0; i < lessons.size(); i++) {
            int lessonVisibleIndex = i + 1;
            Lesson lesson = lessons.get(i);
            lesson.setIndex(lessonVisibleIndex);

            StepicSectionDirBuilder dirBuilder = new StepicSectionDirBuilder(moduleDir, lesson);
            dirBuilder.build();
//
            StepicLessonModuleBuilder stepicLessonModuleBuilder =  new StepicLessonModuleBuilder(dirBuilder.getSectionDir() , lesson, utilModule);
//            StepicLessonModuleBuilder stepicLessonModuleBuilder =  new StepicLessonModuleBuilder(moduleDir, lesson, utilModule);
            stepicLessonModuleBuilder.createModule(moduleModel);
        }
    }


//    @Override
//    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
//        setSourcePaths(Collections.emptyList());
//        super.setupRootModel(rootModel);
//    }

}
