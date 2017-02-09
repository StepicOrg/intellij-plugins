package com.jetbrains.tmp.learning;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.ui.content.Content;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudyUtils {
    private static final String PATH_PATTERN = "^(?:(?:section([0-9]+)/lesson([0-9]+)/step([0-9]+))|(?:lesson([0-9]+)/step([0-9]+))|(?:step([0-9]+))).*$";
    private static Pattern pathPattern;

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
    public static StudyToolWindow getStudyToolWindow(@NotNull final Project project) {
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

        StudyNode lessonNode = stepNode.getParent();
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
        VirtualFile[] files = FileEditorManager.getInstance(project).getOpenFiles();
        if (files.length == 0) {
            return null;
        }

        StudyNode studyNode = getStep(project, files[0]);

        return studyNode instanceof StepNode ? (StepNode) studyNode : null;
    }

    @Nullable
    public static StudyNode getSelectedNode(@NotNull Project project) {
        StepNode stepNode = getSelectedStep(project);
        if (stepNode != null) {
            return stepNode;
        }

        PsiElement node = ProjectView.getInstance(project)
                .getParentOfCurrentSelection();
        if (node == null) {
            return null;
        }

        PsiFileSystemItem file;

        if (node instanceof PsiFileSystemItem) {
            file = (PsiFileSystemItem) node;
        } else {
            file = node.getContainingFile();
        }

        if (file == null) {
            return null;
        }

        return getStep(project, file.getVirtualFile());
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
    public static StudyNode getStep(@NotNull Project project, @NotNull VirtualFile stepVF) {
        String path = getRelativePath(project, stepVF);

        if (pathPattern == null) {
            pathPattern = Pattern.compile(PATH_PATTERN);
        }
        Matcher matcher = pathPattern.matcher(path);
        if (matcher.matches()) {
            return getStudyNode(project, matcher);
        }

        return null;
    }

    @Nullable
    private static StudyNode getStudyNode(@NotNull Project project, Matcher matcher) {
        StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
        if (projectManager == null) {
            return null;
        }
        StudyNode projectRoot = projectManager.getProjectRoot();
        if (projectRoot == null) {
            return null;
        }

        for (int i = 1; i <= matcher.groupCount(); i++) {
            String idString = matcher.group(i);

            if (idString == null) {
                continue;
            }

            int id = Integer.parseInt(idString);

            projectRoot = projectRoot.getChildById(id);
            if (projectRoot == null) {
                return null;
            }
        }

        return projectRoot;
    }
}
