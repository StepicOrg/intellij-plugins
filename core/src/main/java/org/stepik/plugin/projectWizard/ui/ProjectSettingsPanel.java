package org.stepik.plugin.projectWizard.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.StudyObject;
import org.stepik.plugin.projectWizard.StepikProjectGenerator;
import org.stepik.plugin.utils.Utils;

import javax.swing.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.List;

public class ProjectSettingsPanel implements ProjectSetting, HierarchyListener {
    private static final Logger logger = Logger.getInstance(ProjectSettingsPanel.class);
    private final List<ProjectSettingListener> listeners = new ArrayList<>();
    private JPanel mainPanel;
    private JLabel nameLabel;
    private JLabel userName;
    private JLabel langLabel;
    private LanguageComboBox langComboBox;
    private JLabel courseLabel;
    private CourseListBox courseListComboBox;
    private RefreshButton refreshListButton;
    private CourseDescriptionPane courseListDescription;
    private JScrollPane scrollPane;
    private JButton logoutButton;
    private StudyObject selectedStudyObject = StepikProjectGenerator.EMPTY_STUDY_OBJECT;

    public ProjectSettingsPanel(boolean visibleLangBox) {
        refreshListButton.setTarget(courseListComboBox);
        courseListComboBox.setTarget(this);

        langComboBox.setTarget(this);
        langComboBox.setVisible(visibleLangBox);
        langLabel.setVisible(visibleLangBox);

        mainPanel.addHierarchyListener(this);

        logoutButton.addActionListener(e -> {
            StepikConnectorLogin.logoutAndAuth();
            setUsername();
        });
    }

    private void setUsername() {
        String username = StepikConnectorLogin.getCurrentUserFullName();
        userName.setText(username);
    }

    @NotNull
    public JPanel getComponent() {
        return mainPanel;
    }

    public void updateStep() {
        logger.info("Start updating settings");
        courseListComboBox.refresh(langComboBox.getSelectedItem());
        setUsername();
        logger.info("Updating settings is done");
    }

    public boolean validate() {
        boolean valid = !selectedStudyObject.isAdaptive() && selectedStudyObject.getId() != 0;
        logger.info("Validation is " + valid);
        return valid;
    }

    @Override
    public void selectedStudyNode(@NotNull StudyObject studyObject) {
        selectedStudyObject = studyObject;
        String description = Utils.getCourseDescription(studyObject);
        courseListDescription.setText(description);
        // Scroll to top
        courseListDescription.setSelectionStart(0);
        courseListDescription.setSelectionEnd(0);
        setUsername();
        logger.info("Has selected the course: " + studyObject);
        notifyListeners();
    }

    private void notifyListeners() {
        listeners.forEach(ProjectSettingListener::changed);
    }

    @Override
    public void addListener(@NotNull ProjectSettingListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull ProjectSettingListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void selectedProgrammingLanguage(@NotNull SupportedLanguages language) {
        courseListComboBox.refresh(language);
    }

    @NotNull
    public SupportedLanguages getLanguage() {
        return langComboBox.getSelectedItem();
    }

    public void setLanguage(@NotNull SupportedLanguages language) {
        langComboBox.setSelectedItem(language);
    }

    @NotNull
    public StudyObject getSelectedStudyObject() {
        return selectedStudyObject;
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        notifyListeners();
    }
}