package com.jetbrains.tmp.learning.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBCardLayout;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyBasePluginConfigurator;
import com.jetbrains.tmp.learning.StudyPluginConfigurator;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.utils.ProgrammingLanguageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Map;

public abstract class StudyToolWindow extends SimpleToolWindowPanel implements DataProvider, Disposable, ActionListener {
    private static final Logger logger = Logger.getInstance(StudyToolWindow.class);
    private static final String STEP_INFO_ID = "stepInfo";
    private static final String EMPTY_STEP_TEXT = "Please, open any step to see step description";
    private final JComboBox<SupportedLanguages> languageBox;

    private final JBCardLayout cardLayout;
    private final JPanel contentPanel;
    private final OnePixelSplitter splitPane;
    private Project project;
    private StepNode stepNode;

    StudyToolWindow() {
        super(true, true);
        cardLayout = new JBCardLayout();
        contentPanel = new JPanel(cardLayout);
        splitPane = new OnePixelSplitter(myVertical = true);
        languageBox = new JComboBox<>();
        languageBox.setVisible(false);
        languageBox.addActionListener(this);
    }

    @NotNull
    private static DefaultActionGroup getActionGroup(@NotNull final Project project) {
        DefaultActionGroup group = new DefaultActionGroup();
        if (!StepikProjectManager.isStepikProject(project)) {
            logger.warn(project.getName() + " is not Stepik-project");
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

    private JPanel createToolbarPanel(ActionGroup group) {
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("Study", group, true);
        BorderLayoutPanel toolBar = JBUI.Panels.simplePanel(actionToolBar.getComponent());
        toolBar.addToRight(languageBox);
        return toolBar;
    }

    void init(@NotNull final Project project) {
        this.project = project;
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

        StepNode stepNode = StudyUtils.getSelectedStep(project);
        setStepNode(stepNode);
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

    public void setStepNode(@Nullable StepNode stepNode) {
        if (this.stepNode == stepNode) {
            return;
        }

        this.stepNode = stepNode;
        languageBox.removeAllItems();
        languageBox.setVisible(stepNode != null);

        if (stepNode == null) {
            setText(EMPTY_STEP_TEXT);
            return;
        }

        String text = StudyUtils.getStepTextFromStep(stepNode);
        if (text == null) {
            text = EMPTY_STEP_TEXT;
        }

        stepNode.getSupportedLanguages().stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .forEach(languageBox::addItem);
        languageBox.setSelectedItem(stepNode.getCurrentLang());
        languageBox.setVisible(languageBox.getModel().getSize() != 0);

        setText(text);
    }

    protected abstract void setText(@NotNull String text);

    @Override
    public void actionPerformed(ActionEvent e) {
        if (stepNode == null) {
            return;
        }

        SupportedLanguages language = (SupportedLanguages) languageBox.getSelectedItem();

        if (language == null) {
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
                    SupportedLanguages selectedLang = (SupportedLanguages) languageBox.getSelectedItem();
                    ProgrammingLanguageUtils.switchProgrammingLanguage(project, stepNode, selectedLang);
                    if (stepNode != null && selectedLang != stepNode.getCurrentLang()) {
                        languageBox.setSelectedItem(stepNode.getCurrentLang());
                    }
                }
        );
    }
}
