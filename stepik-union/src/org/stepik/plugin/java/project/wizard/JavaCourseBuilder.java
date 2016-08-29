package org.stepik.plugin.java.project.wizard;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.Pair;
import com.jetbrains.edu.learning.StudyProjectComponent;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.core.EduNames;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.courseFormat.Lesson;
import com.jetbrains.edu.learning.stepik.StepikConnectorLogin;
import com.jetbrains.edu.utils.generation.*;
import com.jetbrains.edu.utils.generation.builders.CourseBuilder;
import com.jetbrains.edu.utils.generation.builders.LessonBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JavaCourseBuilder extends JavaModuleBuilder implements CourseBuilder {
    private static final Logger LOG = Logger.getInstance(JavaCourseBuilder.class);
    private StepikProjectGenerator generator;
    private List<Pair<String, String>> mySourcePaths;
    static Module utilModule;

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        return mySourcePaths;
    }

    @Override
    public void createCourseFromGenerator(@NotNull ModifiableModuleModel moduleModel, Project project, EduProjectGenerator generator) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        generator.generateProject(project, project.getBaseDir());

        Course course = StudyTaskManager.getInstance(project).getCourse();
        course.setCourseMode(EduNames.STEPIK_CODE);
        if (course == null) {
            LOG.info("failed to generate builders");
            return;
        }
        String moduleDir = getModuleFileDirectory();
        if (moduleDir == null) {
            return;
        }

        EduUtilModuleBuilder utilModuleBuilder = new EduUtilModuleBuilder(moduleDir);
        utilModule = utilModuleBuilder.createModule(moduleModel);

        createLessonModules(moduleModel, course, moduleDir, utilModule);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(() -> {
                            StudyProjectComponent.getInstance(project).registerStudyToolWindow(course);
                        })));
    }

    @Override
    public void createLessonModules(@NotNull ModifiableModuleModel moduleModel, Course course, String moduleDir, Module utilModule) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        {
            List<Lesson> lessons = course.getLessons();
            for (int i = 0; i < lessons.size(); i++) {
                int lessonVisibleIndex = i + 1;
                Lesson lesson = lessons.get(i);
                lesson.setIndex(lessonVisibleIndex);

                StepikSectionDirBuilder dirBuilder = new StepikSectionDirBuilder(moduleDir, lesson);
                dirBuilder.build();

                LessonBuilder lessonBuilder = new StepikJavaLessonBuilder(dirBuilder.getSectionDir(), lesson, utilModule);
                lessonBuilder.createLesson(moduleModel);
            }
        }
    }

//    @Override
//    public String getBuilderId() {
//        return "java.stepik.builder";
//    }


    @Override
    public ModuleType getModuleType() {
        return StepikModuleType.STEPIK_MODULE_TYPE;
    }

//    @Nullable
//    @Override
//    public Module commitModule(@NotNull Project project, @Nullable ModifiableModuleModel model) {
//        Module baseModule = super.commitModule(project, model);
//        new StepikJavaCourseConfigurator().configureModule(project);
//        return baseModule;
//    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, this, Conditions.alwaysTrue());
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module baseModule = super.createModule(moduleModel);
        Project project = baseModule.getProject();
        LOG.warn("login dialog");
        StepikConnectorLogin.loginFromDialog(project);
        createCourseFromGenerator(moduleModel, project, getGenerator());
        return baseModule;
    }


    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        ModuleWizardStep[] previousWizardSteps = super.createWizardSteps(wizardContext, modulesProvider);
        ModuleWizardStep[] wizardSteps = new ModuleWizardStep[previousWizardSteps.length + 1];
//        ModuleWizardStep[] wizardSteps = new ModuleWizardStep[3];

//        wizardSteps[0] = new StepikModuleWizardStep(getGenerator(), wizardContext);
//        wizardSteps[0] = new StepikProjectPanel(this, wizardContext);
        wizardSteps[0] = new SelectCourseWizardStep(getGenerator(), wizardContext);

//        ProjectSettingsStep myProjectSettingsStep = new ProjectSettingsStep(wizardContext);
//        wizardSteps[1] = ProjectWizardStepFactory.getInstance().createJavaSettingsStep(myProjectSettingsStep, this, this::isSuitableSdkType);
//        wizardSteps[2] = new SdkSettingsStep(myProjectSettingsStep, this, id -> PythonSdkType.getInstance() == id) {
//            @Override
//            protected void onSdkSelected(Sdk sdk) {
//                setSdk(sdk);
//            }
//
//            public void setSdk(final Sdk sdk) {
//                final List<Runnable> mySdkChangedListeners = ContainerUtil.createLockFreeCopyOnWriteList();
//                if (mySdk != sdk) {
//                    mySdk = sdk;
//                    for (Runnable runnable : mySdkChangedListeners) {
//                        runnable.run();
//                    }
//                }
//            }
//        };
//        for (int i = 0; i < previousWizardSteps.length; i++) {
//            wizardSteps[i + 1] = previousWizardSteps[i];
//        }

        return wizardSteps;
    }

    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep) {
        return super.modifyProjectTypeStep(settingsStep);
    }

    private StepikProjectGenerator getGenerator() {
        if (generator == null) {
            generator = new StepikProjectGenerator();
        }
        return generator;
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        setSourcePaths(Collections.emptyList());
        super.setupRootModel(rootModel);
    }
}