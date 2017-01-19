package com.jetbrains.tmp.learning.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBCardLayout;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.util.ui.JBUI;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyBasePluginConfigurator;
import com.jetbrains.tmp.learning.StudyPluginConfigurator;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public abstract class StudyToolWindow extends SimpleToolWindowPanel implements DataProvider, Disposable {
    private static final Logger logger = Logger.getInstance(StudyToolWindow.class);
    private static final String STEP_INFO_ID = "stepInfo";
    private static final String EMPTY_STEP_TEXT = "Please, open any step to see step description";

    private final JBCardLayout cardLayout;
    private final JPanel contentPanel;
    private final OnePixelSplitter splitPane;

    StudyToolWindow() {
        super(true, true);
        cardLayout = new JBCardLayout();
        contentPanel = new JPanel(cardLayout);
        splitPane = new OnePixelSplitter(myVertical = true);
    }

    private static JPanel createToolbarPanel(ActionGroup group) {
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("Study", group, true);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }

    private static DefaultActionGroup getActionGroup(@NotNull final Project project) {
        DefaultActionGroup group = new DefaultActionGroup();
        CourseNode courseNode = StepikProjectManager.getInstance(project).getCourseNode();
        if (courseNode == null) {
            logger.warn("CourseNode is null");
            return group;
        }
        StudyPluginConfigurator configurator = StudyUtils.getConfigurator(project);
        if (configurator != null) {
            group.addAll(configurator.getActionGroup(project));
            return group;
        } else {
            logger.warn("No configurator is provided for plugin");
            return StudyBasePluginConfigurator.getDefaultActionGroup();
        }
    }

    void init(@NotNull final Project project) {
        String stepText = StudyUtils.getStepText(project);
        if (stepText == null) {
            logger.warn("step text is empty");
            return;
        }

        final DefaultActionGroup group = getActionGroup(project);
        setActionToolbar(group);

        final JPanel panel = new JPanel(new BorderLayout());

        JComponent stepInfoPanel = createStepInfoPanel(project);
        panel.add(stepInfoPanel, BorderLayout.CENTER);

        contentPanel.add(STEP_INFO_ID, panel);
        splitPane.setFirstComponent(contentPanel);
        addAdditionalPanels(project);
        cardLayout.show(contentPanel, STEP_INFO_ID);

        setContent(splitPane);

        StudyPluginConfigurator configurator = StudyUtils.getConfigurator(project);
        if (configurator != null) {
            final FileEditorManagerListener listener = configurator.getFileEditorManagerListener(project, this);
            project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener);
        }
        setStepText(stepText);
    }

    private void setActionToolbar(DefaultActionGroup group) {
        JPanel toolbarPanel = createToolbarPanel(group);
        setToolbar(toolbarPanel);
    }

    private void addAdditionalPanels(Project project) {
        StudyPluginConfigurator configurator = StudyUtils.getConfigurator(project);
        if (configurator != null) {
            Map<String, JPanel> panels = configurator.getAdditionalPanels(project);
            for (Map.Entry<String, JPanel> entry : panels.entrySet()) {
                contentPanel.add(entry.getKey(), entry.getValue());
            }
        }
    }

    public void dispose() {
    }

    public abstract JComponent createStepInfoPanel(Project project);

    public void setStepText(String text) {
        setText(text);
    }

    protected abstract void setText(@NotNull String text);

    public void setEmptyText() {
        setStepText(EMPTY_STEP_TEXT);
    }
}
