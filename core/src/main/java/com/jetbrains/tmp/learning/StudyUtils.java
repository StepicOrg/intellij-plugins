package com.jetbrains.tmp.learning;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.ui.content.Content;
import com.jetbrains.tmp.learning.courseFormat.ChoiceStepNodeHelper;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.courseFormat.VideoStepNodeHelper;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import com.jetbrains.tmp.learning.ui.StudyToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.steps.Limit;
import org.stepik.api.objects.steps.Sample;
import org.stepik.api.objects.steps.Step;
import org.stepik.core.templates.Templater;
import org.stepik.core.utils.ProjectFilesUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudyUtils {
    private static final String PATH_PATTERN = "^(?:(?:section([0-9]+)/lesson([0-9]+)/step([0-9]+))|" +
            "(?:lesson([0-9]+)/step([0-9]+))|" +
            "(?:step([0-9]+))|" +
            "(?:section([0-9]+)/lesson([0-9]+))|" +
            "(?:section([0-9]+))" +
            ").*$";
    private static final String UNKNOWN_STEP_TEXT = "This step can take place in the web version (%s)";
    private static final String STEP_LINK_TEXT = "View step on Stepik.org";
    private static final String VIDEO_LINK_TEXT = "Play video in the web version";
    private static final String VIDEO_BLOCK = "<video src=\"%s\" style width=\"100%%\" preload controls autoplay></video>";
    private static final String STEP_LINK_TEMPLATE = "<a href='https://stepik.org/lesson/%d/step/%d'>%s</a>";
    private static Pattern pathPattern;

    private StudyUtils() {
    }

    public static void updateToolWindows(@NotNull final Project project) {
        StudyNode stepNode = getSelectedNode(project);
        setStudyNode(project, stepNode, true);
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

    public static void setStudyNode(@NotNull final Project project, @Nullable StudyNode studyNode) {
        setStudyNode(project, studyNode, false);
    }

    public static void setStudyNode(@NotNull final Project project, @Nullable StudyNode studyNode, boolean force) {
        StudyToolWindow toolWindow = getStudyToolWindow(project);
        if (toolWindow != null) {
            ApplicationManager.getApplication().invokeLater(() -> toolWindow.setStepNode(studyNode, force));
        }
    }

    public static String getVideoStepText(
            @NotNull VideoStepNodeHelper videoStepNode,
            int quality) {
        videoStepNode.setQuality(quality);
        String text = getLink(videoStepNode.getStepNode(), VIDEO_LINK_TEXT);
        if (videoStepNode.hasContent()) {
            return text + "<br>" + String.format(VIDEO_BLOCK, videoStepNode.getUrl());
        }

        return text;
    }

    public static String getChoiceStepText(@NotNull ChoiceStepNodeHelper choiceStepNode) {
        String text = getTextStepText(choiceStepNode.getStepNode());

        HashMap<String, Object> params = new HashMap<>();
        params.put("text", text);
        params.put("choiceStepNode", choiceStepNode);

        return Templater.processTemplate("choice_block", params);
    }

    public static String getTextStepText(@NotNull StepNode stepNode) {
        return getStepText(stepNode, STEP_LINK_TEXT);
    }

    private static String getStepText(@NotNull StepNode stepNode, @NotNull String linkText, Object... params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getLink(stepNode, String.format(linkText, params)));

        if (!stepNode.getText().startsWith("<p>") && !stepNode.getText().startsWith("<h")) {
            stringBuilder.append("<br><br>");
        }

        stringBuilder.append(stepNode.getText());

        return stringBuilder.toString();
    }

    public static String getUnknownStepText(@NotNull StepNode stepNode) {
        Step data = stepNode.getData();
        String stepType = data != null ? data.getBlock().getName() : stepNode.getType().toString();
        return getStepText(stepNode, UNKNOWN_STEP_TEXT, stepType);
    }

    public static String getCodeStepText(@NotNull StepNode stepNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getTextStepText(stepNode));

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

    private static String getLink(@NotNull StepNode stepNode, @NotNull String text) {
        StudyNode lessonNode = stepNode.getParent();
        if (lessonNode != null) {
            return String.format(STEP_LINK_TEMPLATE, lessonNode.getId(), stepNode.getPosition(), text);
        }
        return text;
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

        StudyNode studyNode = getStudyNode(project, files[0]);

        return studyNode instanceof StepNode ? (StepNode) studyNode : null;
    }

    @Nullable
    static StudyNode getSelectedNode(@NotNull Project project) {
        StudyNode studyNode = getSelectedStep(project);

        if (studyNode == null) {
            studyNode = getSelectedNodeInTree(project);
        }

        return studyNode;
    }

    @Nullable
    public static StudyNode getSelectedNodeInTree(@NotNull Project project) {
        PsiElement element = getSelectedPsiElement(project);
        if (element == null) {
            return null;
        }

        PsiFileSystemItem file;

        if (element instanceof PsiFileSystemItem) {
            file = (PsiFileSystemItem) element;
        } else {
            file = element.getContainingFile();
        }

        if (file == null) {
            return null;
        }

        return getStudyNode(project, file.getVirtualFile());
    }

    private static PsiElement getSelectedPsiElement(@NotNull Project project) {
        ProjectView projectView = ProjectView.getInstance(project);
        AbstractProjectViewPane currentProjectViewPane = projectView.getCurrentProjectViewPane();
        if (currentProjectViewPane == null) {
            return null;
        }
        DefaultMutableTreeNode node = currentProjectViewPane.getSelectedNode();
        if (node == null) {
            return null;
        }

        Object userObject = node.getUserObject();
        if (userObject instanceof ProjectViewNode) {
            ProjectViewNode descriptor = (ProjectViewNode) userObject;
            Object element = descriptor.getValue();
            if (element instanceof PsiElement) {
                PsiElement psiElement = (PsiElement) element;
                return !psiElement.isValid() ? null : psiElement;
            } else {
                return null;
            }
        } else {
            return null;
        }
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
    public static StudyNode getStudyNode(@NotNull Project project, @NotNull VirtualFile nodeVF) {
        String path = getRelativePath(project, nodeVF);

        StudyNode root = StepikProjectManager.getProjectRoot(project);
        if (root == null) {
            return null;
        }

        return getStudyNode(root, path);
    }

    @Nullable
    public static StudyNode getStudyNode(@NotNull StudyNode root, @NotNull String relativePath) {
        if (relativePath.equals(".")) {
            return root;
        }

        if (pathPattern == null) {
            pathPattern = Pattern.compile(PATH_PATTERN);
        }
        Matcher matcher = pathPattern.matcher(relativePath);
        if (!matcher.matches()) {
            return null;
        }

        for (int i = 1; i <= matcher.groupCount(); i++) {
            String idString = matcher.group(i);

            if (idString == null) {
                continue;
            }

            int id = Integer.parseInt(idString);

            root = root.getChildById(id);
            if (root == null) {
                return null;
            }
        }

        return root;
    }
}
