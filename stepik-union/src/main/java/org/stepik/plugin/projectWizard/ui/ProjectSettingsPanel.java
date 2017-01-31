package org.stepik.plugin.projectWizard.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.courses.Course;
import org.stepik.plugin.projectWizard.StepikProjectGenerator;
import org.stepik.plugin.utils.Utils;

import javax.swing.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.List;

public class ProjectSettingsPanel implements ProjectSetting, HierarchyListener {
    private static final Logger logger = Logger.getInstance(ProjectSettingsPanel.class);
    private final Project project;
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
    private Course selectedCourse = StepikProjectGenerator.EMPTY_COURSE;

    public ProjectSettingsPanel(@NotNull Project project, boolean visibleLangBox) {
        this.project = project;
        refreshListButton.setTarget(courseListComboBox, project);
        courseListComboBox.setTarget(this);

        langComboBox.setTarget(this);
        langComboBox.setVisible(visibleLangBox);
        langLabel.setVisible(visibleLangBox);

        mainPanel.addHierarchyListener(this);
    }

    @NotNull
    public JPanel getComponent() {
        return mainPanel;
    }

    public void updateStep() {
        logger.info("Start updating settings");
        StepikConnectorLogin.authentication();
        String username = StepikConnectorLogin.getCurrentUserFullName();
        userName.setText(username);
        courseListComboBox.refresh(project, langComboBox.getSelectedItem());
        logger.info("Updating settings is done");
    }

    public boolean validate() {
        boolean valid = !selectedCourse.isAdaptive() && selectedCourse.getId() != 0;
        logger.info("Validation is " + valid);
        return valid;
    }

    @Override
    public void selectedCourse(@NotNull Course course) {
        selectedCourse = course;
        String description = Utils.getCourseDescription(course);
        courseListDescription.setText(description);
        // Scroll to top
        courseListDescription.setSelectionStart(0);
        courseListDescription.setSelectionEnd(0);
        logger.info("Has selected the course: " + course);
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
        courseListComboBox.refresh(project, language);
    }

    @NotNull
    public SupportedLanguages getLanguage() {
        return langComboBox.getSelectedItem();
    }

    public void setLanguage(@NotNull SupportedLanguages language) {
        langComboBox.setSelectedItem(language);
    }

    @NotNull
    public Course getSelectedCourse() {
        return selectedCourse;
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        notifyListeners();
    }
}