package org.stepik.plugin.projectWizard.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.courses.Course;
import org.stepik.plugin.utils.Utils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectSettingsPanel implements ProjectSetting {
    private static final Logger logger = Logger.getInstance(ProjectSettingsPanel.class);
    private final Project project;
    private final List<ProjectSettingListener> listeners = new ArrayList<>();
    private JPanel mainPanel;
    private JLabel nameLabel;
    private JLabel userName;
    private JLabel langLabel;
    private LanguageComboBox langComboBox;
    private JLabel buildLabel;
    private BuildTypeComboBox buildType;
    private JLabel courseLabel;
    private CourseListBox courseListComboBox;
    private RefreshButton refreshListButton;
    private CourseDescriptionPane courseListDescription;
    private JScrollPane scrollPane;
    private Course selectedCourse = StepikProjectGenerator.EMPTY_COURSE;

    public ProjectSettingsPanel(@NotNull Project project, boolean visibleLangBox) {
        this.project = project;
        buildType.setTarget(this);
        refreshListButton.setTarget(courseListComboBox, project);
        courseListComboBox.setTarget(this);

        langComboBox.setVisible(visibleLangBox);
        langLabel.setVisible(visibleLangBox);
    }

    @NotNull
    public JPanel getComponent() {
        return mainPanel;
    }

    public void updateStep() {
        logger.info("Start updating settings");
        StepikConnectorLogin.loginFromDialog(project);
        String username = StepikConnectorLogin.getCurrentUserFullName();
        userName.setText(username);
        courseListComboBox.refresh(project);
        logger.info("Updating settings is done");
    }

    public boolean validate() {
        boolean valid = !selectedCourse.isAdaptive() && selectedCourse.getId() != 0;
        logger.info("Validation is " + valid);
        return valid;
    }

    @Override
    public void selectedBuildType(@NotNull BuildType type) {
        if (type == BuildType.COURSE_LINK) {
            courseListComboBox.getModel().setSelectedItem("");
            courseListComboBox.requestFocus(true);
        }
        courseListComboBox.setEditable(BuildType.COURSE_LINK == type);
        courseLabel.setText(type + ":");
        logger.info("Has selected the build type: " + type);
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
}