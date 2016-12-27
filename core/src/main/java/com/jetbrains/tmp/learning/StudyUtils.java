package com.jetbrains.tmp.learning;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.util.TimeoutUtil;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Step;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import com.jetbrains.tmp.learning.ui.StudyToolWindowFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.utils.ProjectFilesUtils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudyUtils {
    private static final Logger logger = Logger.getInstance(StudyUtils.class.getName());
    private static final String EMPTY_STEP_TEXT = "Please, open any step to see step description";
    private static Pattern stepPathPattern;

    private StudyUtils() {
    }

    public static void updateAction(@NotNull final AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        presentation.setEnabled(false);
        final Project project = e.getProject();
        if (project != null) {
            final StudyEditor studyEditor = getSelectedStudyEditor(project);
            if (studyEditor != null) {
                presentation.setEnabledAndVisible(true);
            }
        }
    }

    public static void updateToolWindows(@NotNull final Project project) {
        final StudyToolWindow studyToolWindow = getStudyToolWindow(project);
        if (studyToolWindow != null) {
            String stepText = getStepText(project);
            if (stepText != null) {
                studyToolWindow.setStepText(stepText);
            } else {
                logger.warn("Step text is null");
            }
        }
    }

    static void initToolWindows(@NotNull final Project project) {
        final ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
        windowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW)
                .getContentManager()
                .removeAllContents(false);
        StudyToolWindowFactory factory = new StudyToolWindowFactory();
        factory.createToolWindowContent(project, windowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW));

    }

    @Nullable
    static StudyToolWindow getStudyToolWindow(@NotNull final Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project)
                .getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
        if (toolWindow != null) {
            Content[] contents = toolWindow.getContentManager().getContents();
            for (Content content : contents) {
                JComponent component = content.getComponent();
                if (component != null && component instanceof StudyToolWindow) {
                    return (StudyToolWindow) component;
                }
            }
        }
        return null;
    }

    /**
     * shows pop up in the center of "check step" button in study editor
     */
    public static void showCheckPopUp(@NotNull final Project project, @NotNull final Balloon balloon) {
        final StudyEditor studyEditor = getSelectedStudyEditor(project);
        assert studyEditor != null;

        balloon.show(computeLocation(studyEditor.getEditor()), Balloon.Position.above);
        Disposer.register(project, balloon);
    }

    public static RelativePoint computeLocation(Editor editor) {

        final Rectangle visibleRect = editor.getComponent().getVisibleRect();
        Point point = new Point(visibleRect.x + visibleRect.width + 10,
                visibleRect.y + 10);
        return new RelativePoint(editor.getComponent(), point);
    }

    @Nullable
    public static StepFile getStepFile(@NotNull final Project project, @NotNull final VirtualFile file) {
        final Course course = StepikProjectManager.getInstance(project).getCourse();
        if (course == null) {
            return null;
        }
        VirtualFile stepDir = file.getParent();
        if (stepDir == null) {
            return null;
        }
        //need this because of multi-module generation
        if (EduNames.SRC.equals(stepDir.getName())) {
            stepDir = stepDir.getParent();
            if (stepDir == null) {
                return null;
            }
        }
        final String stepDirName = stepDir.getName();
        if (stepDirName.contains(EduNames.STEP)) {
            int stepId = EduUtils.parseDirName(stepDirName, EduNames.STEP);
            final Step step = course.getStepById(stepId);
            if (step == null) {
                return null;
            }
            return step.getFile(file.getName());
        }
        return null;
    }

    @Nullable
    public static StudyEditor getSelectedStudyEditor(@NotNull final Project project) {
        try {
            final FileEditor fileEditor = FileEditorManagerEx.getInstanceEx(project).getSplitters().getCurrentWindow().
                    getSelectedEditor().getSelectedEditorWithProvider().getFirst();
            if (fileEditor instanceof StudyEditor) {
                return (StudyEditor) fileEditor;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Nullable
    public static Editor getSelectedEditor(@NotNull final Project project) {
        final StudyEditor studyEditor = getSelectedStudyEditor(project);
        if (studyEditor != null) {
            return studyEditor.getEditor();
        }
        return null;
    }

    @Nullable
    @Contract("null -> null")
    static String getStepTextFromStep(@Nullable final Step step) {
        if (step == null) {
            return null;
        }
        return getTextWithStepLink(step);
    }

    @NotNull
    private static String getTextWithStepLink(Step step) {
        StringBuilder stringBuilder = new StringBuilder();

        Lesson lesson = step.getLesson();
        if (lesson != null) {
            stringBuilder.append("<a href=\"https://stepik.org/lesson/")
                    .append(lesson.getId())
                    .append("/step/")
                    .append(step.getPosition())
                    .append("\">View step on Stepik.org</a>");
        }

        if (!step.getText().startsWith("<p>") && !step.getText().startsWith("<h")) {
            stringBuilder.append("<br><br>");
        }

        stringBuilder.append(step.getDescription());
        return stringBuilder.toString();
    }

    @Nullable
    public static StudyPluginConfigurator getConfigurator(@NotNull final Project project) {
        StudyPluginConfigurator[] extensions = StudyPluginConfigurator.EP_NAME.getExtensions();
        for (StudyPluginConfigurator extension : extensions) {
            if (extension.accept(project)) {
                return extension;
            }
        }
        return null;
    }

    @Nullable
    public static String getStepText(@NotNull final Project project) {
        final Step step = getSelectedStep(project);
        if (step != null) {
            return getStepTextFromStep(step);
        }
        return EMPTY_STEP_TEXT;
    }

    private static String getRelativePath(@NotNull Project project, @NotNull VirtualFile item) {
        String path = item.getPath();
        String basePath = project.getBasePath();

        if (basePath == null) {
            return path;
        }

        return ProjectFilesUtils.getRelativePath(basePath, path);
    }

    @Nullable
    public static Step getSelectedStep(@NotNull Project project) {
        VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles();
        if (files.length == 0) {
            return null;
        }

        return getStep(project, files[0]);
    }

    @Nullable
    public static Project getStudyProject() {
        Project studyProject = null;
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : openProjects) {
            if (StepikProjectManager.getInstance(project).getCourse() != null) {
                studyProject = project;
                break;
            }
        }
        if (studyProject == null) {
            logger.info("return default project");
            return ProjectManager.getInstance().getDefaultProject();
        }
        logger.info("return regular project");
        return studyProject;
    }

    public static boolean hasJavaFx() {
        try {
            Class.forName("javafx.application.Platform");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Nullable
    static Step getStep(@NotNull Project project, @NotNull VirtualFile stepVF) {
        String path = getRelativePath(project, stepVF);
        if (stepPathPattern == null) {
            stepPathPattern = Pattern.compile("^(section[0-9]+)/(lesson[0-9]+)/(step[0-9]+)/src/.*");
        }
        Matcher matcher = stepPathPattern.matcher(path);
        if (matcher.matches()) {
            Course course = StepikProjectManager.getInstance(project).getCourse();
            if (course == null) {
                return null;
            }
            Lesson lesson = course.getLessonByDirName(matcher.group(2));
            if (lesson == null) {
                return null;
            }
            return lesson.getStep(matcher.group(3));
        }
        return null;
    }

    // supposed to be called under progress
    @Nullable
    public static <T> T execCancelable(@NotNull final Callable<T> callable) {
        final Future<T> future = ApplicationManager.getApplication().executeOnPooledThread(callable);

        while (!future.isCancelled() && !future.isDone()) {
            ProgressManager.checkCanceled();
            TimeoutUtil.sleep(500);
        }
        T result = null;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.warn(e.getMessage());
        }
        return result;
    }
}
