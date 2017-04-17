package org.stepik.core;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.objects.recommendations.Recommendation;
import org.stepik.api.objects.recommendations.Recommendations;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Steps;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.courseFormat.stepHelpers.StepHelper;
import org.stepik.core.templates.Templater;
import org.stepik.core.ui.StudyToolWindow;
import org.stepik.core.ui.StudyToolWindowFactory;
import org.stepik.core.utils.ProjectFilesUtils;

import javax.swing.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.isAuthenticated;

public class StudyUtils {
    private static final Logger logger = Logger.getInstance(StudyUtils.class);
    private static final String PATH_PATTERN = "^(?:(?:section([0-9]+)/lesson([0-9]+)/step([0-9]+))|" +
            "(?:lesson([0-9]+)/step([0-9]+))|" +
            "(?:step([0-9]+))|" +
            "(?:section([0-9]+)/lesson([0-9]+))|" +
            "(?:section([0-9]+))" +
            ").*$";
    private static Pattern pathPattern;

    private StudyUtils() {
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
        if (project.isDisposed()) {
            return null;
        }
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

    @NotNull
    public static String getStepContent(@NotNull StepHelper stepHelper) {
        return processTemplate(stepHelper, "quiz/" + stepHelper.getType());
    }

    @NotNull
    private static String processTemplate(@NotNull StepHelper stepHelper, @NotNull String templateName) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("stepNode", stepHelper);
        params.put("darcula", LafManager.getInstance().getCurrentLookAndFeel() instanceof DarculaLookAndFeelInfo);

        return Templater.processTemplate(templateName, params);
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

    @NotNull
    private static String getRelativePath(@NotNull Project project, @NotNull VirtualFile item) {
        String path = item.getPath();
        String basePath = project.getBasePath();

        if (basePath == null) {
            return path;
        }

        return ProjectFilesUtils.getRelativePath(basePath, path);
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

    @Nullable
    public static StudyNode<?, ?> getRecommendation(@NotNull StudyNode root) {
        StudyObject data = root.getData();
        if (data == null || !data.isAdaptive()) {
            return null;
        }

        StudyNode studyNode = null;
        try {
            StepikApiClient stepikClient = authAndGetStepikApiClient();
            if (!isAuthenticated()) {
                return null;
            }
            Recommendations recommendations = stepikClient.recommendations()
                    .get()
                    .course(root.getId())
                    .execute();
            if (!recommendations.isEmpty()) {
                Recommendation recommendation = recommendations.getFirst();

                long lesson = recommendation.getLesson();

                Steps steps = stepikClient.steps()
                        .get()
                        .lesson(lesson)
                        .execute();
                if (!steps.isEmpty()) {
                    long stepId = steps.getFirst().getId();
                    studyNode = root.getChildByClassAndId(Step.class, stepId);
                }
            }
        } catch (StepikClientException e) {
            logger.warn(e);
        }
        return studyNode;
    }
}
