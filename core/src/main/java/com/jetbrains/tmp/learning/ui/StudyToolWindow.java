package com.jetbrains.tmp.learning.ui;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.util.PropertiesComponent;
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
import com.jetbrains.tmp.learning.courseFormat.StepType;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.courseFormat.stepHelpers.VideoStepNodeHelper;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.core.utils.ProgrammingLanguageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Map;

import static com.jetbrains.tmp.learning.StudyUtils.getChoiceStepText;
import static com.jetbrains.tmp.learning.StudyUtils.getCodeStepText;
import static com.jetbrains.tmp.learning.StudyUtils.getMatchingStepText;
import static com.jetbrains.tmp.learning.StudyUtils.getNumberStepText;
import static com.jetbrains.tmp.learning.StudyUtils.getSortingStepText;
import static com.jetbrains.tmp.learning.StudyUtils.getStringStepText;
import static com.jetbrains.tmp.learning.StudyUtils.getTextStepText;
import static com.jetbrains.tmp.learning.StudyUtils.getUnknownStepText;
import static com.jetbrains.tmp.learning.StudyUtils.getVideoStepText;
import static com.jetbrains.tmp.learning.courseFormat.StepType.TEXT;
import static com.jetbrains.tmp.learning.courseFormat.StepType.VIDEO;
import static org.stepik.core.utils.PluginUtils.PLUGIN_ID;

public abstract class StudyToolWindow extends SimpleToolWindowPanel implements DataProvider, Disposable, ActionListener {
    private static final Logger logger = Logger.getInstance(StudyToolWindow.class);
    private static final String STEP_INFO_ID = "stepInfo";
    private static final String EMPTY_STEP_TEXT = "Please, open any step to see step description";
    private static final String VIDEO_QUALITY_PROPERTY_NAME = PLUGIN_ID + ".VIDEO_QUALITY";
    private final JComboBox<SupportedLanguages> languageBox;
    private final JComboBox<Integer> videoQualityBox;

    private final JBCardLayout cardLayout;
    private final JPanel contentPanel;
    private final OnePixelSplitter splitPane;
    private final Panel rightPanel;
    private final CardLayout layout;
    private final ActionListener qualityListener;
    private Project project;
    private StepNode stepNode;

    StudyToolWindow() {
        super(true, true);
        cardLayout = new JBCardLayout();
        contentPanel = new JPanel(cardLayout);
        splitPane = new OnePixelSplitter(myVertical = true);
        languageBox = new JComboBox<>();
        languageBox.addActionListener(this);

        qualityListener = e -> setText();
        videoQualityBox = new JComboBox<>();

        layout = new CardLayout();
        rightPanel = new Panel(layout);
        rightPanel.add("language", languageBox);
        rightPanel.add("quality", videoQualityBox);
        rightPanel.setVisible(false);
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
        toolBar.addToRight(rightPanel);
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

        videoQualityBox.removeAllItems();
        videoQualityBox.addItem(loadVideoQuality());
        videoQualityBox.setSelectedIndex(0);

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
        setStepNode(null);
    }

    public abstract JComponent createStepInfoPanel(Project project);

    public void setStepNode(@Nullable StudyNode studyNode) {
        setStepNode(studyNode, false);
    }

    public void setStepNode(@Nullable StudyNode studyNode, boolean force) {
        if (!force && stepNode == studyNode) {
            return;
        }

        if (studyNode != null && !(studyNode instanceof StepNode)) {
            setText(EMPTY_STEP_TEXT);
            stepNode = null;
            rightPanel.setVisible(false);
            return;
        }

        stepNode = (StepNode) studyNode;

        setText();
    }

    private void setText() {
        if (stepNode == null) {
            setText(EMPTY_STEP_TEXT);
            rightPanel.setVisible(false);
            return;
        }
        String text;
        boolean rightPanelVisible = false;
        StepType stepType = stepNode.getType();
        boolean theory = stepType == VIDEO || stepType == TEXT;
        postView(theory);

        switch (stepType) {
            case UNKNOWN:
                text = getUnknownStepText(stepNode);
                break;
            case CODE:
                text = getCodeStepText(stepNode);
                languageBox.removeAllItems();
                stepNode.getSupportedLanguages().stream()
                        .sorted(Comparator.comparingInt(Enum::ordinal))
                        .forEach(languageBox::addItem);
                layout.show(rightPanel, "language");
                rightPanelVisible = languageBox.getModel().getSize() != 0;
                languageBox.setSelectedItem(stepNode.getCurrentLang());
                break;
            case TEXT:
                text = getTextStepText(stepNode);
                break;
            case VIDEO:
                VideoStepNodeHelper videoStepNode = stepNode.asVideoStep();
                text = getVideoStepText(videoStepNode, getVideoQuality());
                videoQualityBox.removeActionListener(qualityListener);
                videoQualityBox.removeAllItems();
                videoStepNode.getQualitySet().forEach(videoQualityBox::addItem);
                int quality = videoStepNode.getQuality();
                videoQualityBox.setSelectedItem(quality);
                videoQualityBox.addActionListener(qualityListener);
                layout.show(rightPanel, "quality");
                rightPanelVisible = videoQualityBox.getModel().getSize() != 0;
                storeVideoQuality(quality);
                break;
            case CHOICE:
                text = getChoiceStepText(stepNode);
                break;
            case STRING:
                text = getStringStepText(stepNode);
                break;
            case SORTING:
                text = getSortingStepText(stepNode);
                break;
            case MATCHING:
                text = getMatchingStepText(stepNode);
                break;
            case NUMBER:
                text = getNumberStepText(stepNode);
                break;
            default:
                text = EMPTY_STEP_TEXT;
                break;
        }

        rightPanel.setVisible(rightPanelVisible);
        setText(text);
        ProjectView.getInstance(project).refresh();
    }

    private void postView(boolean needPassed) {
        StepNode finalStepNode = stepNode;
        new Thread(() -> {
            Long assignment = finalStepNode.getAssignment();
            long stepId = finalStepNode.getId();
            try {
                if (assignment != 0) {
                    StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
                    stepikApiClient.views()
                            .post()
                            .assignment(assignment)
                            .step(stepId)
                            .execute();
                }
            } catch (StepikClientException e) {
                logger.warn("Failed post view: stepId=" + stepId + "; assignment=" + assignment, e);
            }

            if (needPassed) {
                finalStepNode.passed();
            }
        }).run();
    }

    private int loadVideoQuality() {
        return Integer.parseInt(PropertiesComponent.getInstance()
                .getValue(VIDEO_QUALITY_PROPERTY_NAME, String.valueOf(0)));
    }

    private void storeVideoQuality(int quality) {
        PropertiesComponent.getInstance().setValue(VIDEO_QUALITY_PROPERTY_NAME, String.valueOf(quality));
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

        final StepNode targetNode = stepNode;

        ApplicationManager.getApplication().invokeLater(() -> {
                    SupportedLanguages selectedLang = (SupportedLanguages) languageBox.getSelectedItem();
                    if (selectedLang != null) {
                        ProgrammingLanguageUtils.switchProgrammingLanguage(project, targetNode, selectedLang);
                        if (selectedLang != targetNode.getCurrentLang()) {
                            languageBox.setSelectedItem(targetNode.getCurrentLang());
                        }
                    }
                }
        );
    }

    private int getVideoQuality() {
        Integer quality = (Integer) videoQualityBox.getSelectedItem();
        if (quality == null) {
            return 0;
        }
        return quality;
    }
}
