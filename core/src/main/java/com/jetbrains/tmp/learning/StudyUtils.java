package com.jetbrains.tmp.learning;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.util.TimeoutUtil;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import com.jetbrains.tmp.learning.ui.StudyToolWindowFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.steps.Limit;
import org.stepik.api.objects.steps.Sample;
import org.stepik.core.utils.ProjectFilesUtils;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudyUtils {
    private static final Logger logger = Logger.getInstance(StudyUtils.class.getName());
    private static Pattern stepPathPattern;

    private StudyUtils() {
    }

    public static void updateToolWindows(@NotNull final Project project) {
        final StudyToolWindow studyToolWindow = getStudyToolWindow(project);
        if (studyToolWindow != null) {
            StepNode stepNode = getSelectedStep(project);
            studyToolWindow.setStepNode(stepNode);
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

    @Nullable
    public static StepFile getStepFile(@NotNull final Project project, @NotNull final VirtualFile file) {
        StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
        if (projectManager == null) {
            return null;
        }
        final CourseNode courseNode = projectManager.getCourseNode();
        if (courseNode == null) {
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
            final StepNode stepNode = courseNode.getStepById(stepId);
            if (stepNode == null) {
                return null;
            }
            return stepNode.getFile(file.getName());
        }
        return null;
    }

    @Nullable
    @Contract("null -> null")
    public static String getStepTextFromStep(@Nullable final StepNode stepNode) {
        if (stepNode == null) {
            return null;
        }
        return getTextWithStepLink(stepNode);
    }

    @NotNull
    private static String getTextWithStepLink(StepNode stepNode) {
        StringBuilder stringBuilder = new StringBuilder();

        LessonNode lessonNode = stepNode.getLessonNode();
        if (lessonNode != null) {
            stringBuilder.append("<a href=\"https://stepik.org/lesson/")
                    .append(lessonNode.getId())
                    .append("/step/")
                    .append(stepNode.getPosition())
                    .append("\">View step on Stepik.org</a>");
        }

        if (!stepNode.getText().startsWith("<p>") && !stepNode.getText().startsWith("<h")) {
            stringBuilder.append("<br><br>");
        }

        stringBuilder.append(stepNode.getText());

        List<Sample> samples = stepNode.getSamples();

        for (int i = 1; i <= samples.size(); i++) {
            Sample sample = samples.get(i - 1);
            stringBuilder.append("<p><b>Sample Input ")
                    .append(i)
                    .append(":</b><br>")
                    .append(sample.getInput().replaceAll("\\n", "<br>"))
                    .append("<br>")
                    .append("<b>Sample Output ")
                    .append(i)
                    .append(":</b><br>")
                    .append(sample.getOutput().replaceAll("\\n", "<br>"))
                    .append("<br>");
        }

        Limit limit = stepNode.getLimit();
        stringBuilder.append("<p><b>Limits: </b>")
                .append(limit.getTime())
                .append("s; ")
                .append(limit.getMemory())
                .append("Mib</p>");

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

    private static String getRelativePath(@NotNull Project project, @NotNull VirtualFile item) {
        String path = item.getPath();
        String basePath = project.getBasePath();

        if (basePath == null) {
            return path;
        }

        return ProjectFilesUtils.getRelativePath(basePath, path);
    }

    @Nullable
    public static StepNode getSelectedStep(@NotNull Project project) {
        VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles();
        if (files.length == 0) {
            return null;
        }

        return getStep(project, files[0]);
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
    public static StepNode getStep(@NotNull Project project, @NotNull VirtualFile stepVF) {
        String path = getRelativePath(project, stepVF);
        if (stepPathPattern == null) {
            stepPathPattern = Pattern.compile("^(section[0-9]+)/(lesson[0-9]+)/(step[0-9]+)/src/.*");
        }
        Matcher matcher = stepPathPattern.matcher(path);
        if (matcher.matches()) {
            StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
            if (projectManager == null) {
                return null;
            }
            CourseNode courseNode = projectManager.getCourseNode();
            if (courseNode == null) {
                return null;
            }
            LessonNode lessonNode = courseNode.getLessonByDirName(matcher.group(2));
            if (lessonNode == null) {
                return null;
            }
            return lessonNode.getStep(matcher.group(3));
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
