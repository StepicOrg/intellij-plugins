package com.jetbrains.tmp.utils.generation.builders;

import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.utils.generation.EduProjectGenerator;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface CourseBuilder {

    void createCourseFromGenerator(@NotNull ModifiableModuleModel moduleModel, Project project, EduProjectGenerator generator) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException;

    void createLessonModules(@NotNull ModifiableModuleModel moduleModel, Course course, String moduleDir, Module utilModule) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException;
}
