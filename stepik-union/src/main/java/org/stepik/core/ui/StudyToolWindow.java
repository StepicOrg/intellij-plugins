package org.stepik.core.ui;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.StudyBasePluginConfigurator;
import org.stepik.core.StudyPluginConfigurator;
import org.stepik.core.StudyUtils;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StepType;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.courseFormat.stepHelpers.VideoStepNodeHelper;
import org.stepik.core.stepik.StepikConnectorLogin;
import org.stepik.core.utils.ProgrammingLanguageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.stepik.core.StudyUtils.getChoiceStepText;
import static org.stepik.core.StudyUtils.getCodeStepText;
import static org.stepik.core.StudyUtils.getDatasetStepText;
import static org.stepik.core.StudyUtils.getFillBlanksStepText;
import static org.stepik.core.StudyUtils.getMatchingStepText;
import static org.stepik.core.StudyUtils.getMathStepText;
import static org.stepik.core.StudyUtils.getNumberStepText;
import static org.stepik.core.StudyUtils.getSortingStepText;
import static org.stepik.core.StudyUtils.getStringStepText;
import static org.stepik.core.StudyUtils.getTableStepText;
import static org.stepik.core.StudyUtils.getTextStepText;
import static org.stepik.core.StudyUtils.getUnknownStepText;
import static org.stepik.core.StudyUtils.getVideoStepText;
import static org.stepik.core.courseFormat.StepType.CODE;
import static org.stepik.core.courseFormat.StepType.TEXT;
import static org.stepik.core.courseFormat.StepType.VIDEO;
import static org.stepik.core.utils.PluginUtils.PLUGIN_ID;

public class StudyToolWindow extends SimpleToolWindowPanel implements DataProvider, Disposable, ActionListener {
    private static final Logger logger = Logger.getInstance(StudyToolWindow.class);
    private static final String STEP_INFO_ID = "stepInfo";
    private static final String EMPTY_STEP_TEXT = "Please, open any step to see step description";
    private static final String VIDEO_QUALITY_PROPERTY_NAME = PLUGIN_ID + ".VIDEO_QUALITY";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
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
    private StudyBrowserWindow browserWindow;

    StudyToolWindow() {
        super(true, true);
        cardLayout = new JBCardLayout();
        contentPanel = new JPanel(cardLayout);
        splitPane = new OnePixelSplitter(myVertical = true);
        languageBox = new JComboBox<>();
        languageBox.addActionListener(this);

        qualityListener = e -> setText(stepNode);
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

    private JComponent createStepInfoPanel(Project project) {
        browserWindow = new StudyBrowserWindow(project);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(browserWindow.getPanel());
        return panel;
    }

    private void setText(@NotNull String text) {
        browserWindow.loadContent(text);
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
            final FileEditorManagerListener listener = configurator.getFileEditorManagerListener(project);
            project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener);
        }

        videoQualityBox.removeAllItems();
        videoQualityBox.addItem(loadVideoQuality());
        videoQualityBox.setSelectedIndex(0);

        StudyNode<?, ?> stepNode = StepikProjectManager.getSelected(project);
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

    private void setStepNode(@Nullable StudyNode studyNode) {
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

        executor.execute(() -> setText(stepNode));
    }

    private void setText(@Nullable StepNode stepNode) {
        if (stepNode == null) {
            setText(EMPTY_STEP_TEXT);
            rightPanel.setVisible(false);
            return;
        }
        String text;
        StepType stepType = stepNode.getType();
        if (stepType != VIDEO && stepType != CODE) {
            SwingUtilities.invokeLater(() -> rightPanel.setVisible(false));
        }
        boolean theory = stepType == VIDEO || stepType == TEXT;
        postView(stepNode, theory);

        switch (stepType) {
            case UNKNOWN:
                text = getUnknownStepText(stepNode);
                break;
            case CODE:
                text = getCodeStepText(stepNode);
                SwingUtilities.invokeLater(() -> {
                    languageBox.removeAllItems();
                    stepNode.getSupportedLanguages().stream()
                            .sorted(Comparator.comparingInt(Enum::ordinal))
                            .forEach(languageBox::addItem);
                    layout.show(rightPanel, "language");
                    boolean rightPanelVisible = languageBox.getModel().getSize() != 0;
                    rightPanel.setVisible(rightPanelVisible);
                    languageBox.setSelectedItem(stepNode.getCurrentLang());
                });
                break;
            case TEXT:
                text = getTextStepText(stepNode);
                break;
            case VIDEO:
                VideoStepNodeHelper videoStepNode = stepNode.asVideoStep();
                text = getVideoStepText(videoStepNode, getVideoQuality());
                SwingUtilities.invokeLater(() -> {
                    videoQualityBox.removeActionListener(qualityListener);
                    videoQualityBox.removeAllItems();
                    videoStepNode.getQualitySet().forEach(videoQualityBox::addItem);
                    int quality = videoStepNode.getQuality();
                    storeVideoQuality(quality);
                    videoQualityBox.setSelectedItem(quality);
                    videoQualityBox.addActionListener(qualityListener);
                    layout.show(rightPanel, "quality");
                    boolean rightPanelVisible = videoQualityBox.getModel().getSize() != 0;
                    rightPanel.setVisible(rightPanelVisible);
                });
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
            case DATASET:
                text = getDatasetStepText(stepNode);
                break;
            case TABLE:
                text = getTableStepText(stepNode);
                break;
            case FILL_BLANKS:
                text = getFillBlanksStepText(stepNode);
                break;
            case MATH:
                text = getMathStepText(stepNode);
                break;
            default:
                text = EMPTY_STEP_TEXT;
                break;
        }

        setText(text);
    }

    private void postView(@NotNull StepNode stepNode, boolean needPassed) {
        executor.execute(() -> {
            Long assignment = stepNode.getAssignment();
            long stepId = stepNode.getId();
            try {
                if (assignment != null && assignment != 0) {
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
                stepNode.passed();
            }

            if (stepNode.getProject() == null) {
                stepNode.setProject(project);
            }
            if (!project.isDisposed()) {
                ProjectView.getInstance(project).refresh();
            }
        });
    }

    private int loadVideoQuality() {
        return Integer.parseInt(PropertiesComponent.getInstance()
                .getValue(VIDEO_QUALITY_PROPERTY_NAME, String.valueOf(0)));
    }

    private void storeVideoQuality(int quality) {
        PropertiesComponent.getInstance().setValue(VIDEO_QUALITY_PROPERTY_NAME, String.valueOf(quality));
    }

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
