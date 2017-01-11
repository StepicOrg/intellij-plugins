package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.ValidationResult;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.HyperlinkAdapter;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.courses.Courses;
import org.stepik.plugin.utils.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

class PyCCSettingPanel {
    final static String COURSE_LINK = "Course link";
    private final static String COURSE_LIST = "Course list";
    private final ValidationResult invalidCourse = new ValidationResult("Please select a course");
    private final ValidationResult adaptiveCourse = new ValidationResult(
            "Sorry, but we don't support adaptive courses yet");
    private final StepikPyProjectGenerator generator;
    private JPanel mainPanel;
    private JLabel nameLabel;
    private JLabel userName;
    private JLabel buildLabel;
    private JComboBox<String> buildType;
    private JPanel courseSelectPanel;
    private JPanel courseLinkPanel;
    private JLabel courseLinkLabel;
    private JTextField courseLinkFiled;
    private JTextPane courseLinkDescription;
    private JPanel courseListPanel;
    private JLabel courseListLabel;
    private JComboBox<Course> courseListComboBox;
    private JButton refreshListButton;
    private JTextPane courseListDescription;
    private Course selectedCourse;
    private Project project;
    private boolean isInit = false;
    private Course courseFromLink = StepikProjectGenerator.EMPTY_COURSE;
    private FacetValidatorsManager validationManager;

    PyCCSettingPanel(StepikPyProjectGenerator generator) {
        this.generator = generator;
    }

    void init(Project project) {
        this.project = project;
        if (!isInit) {
            layoutPanel();
            initListeners();
            isInit = true;
        }
        setupGeneralSettings();
    }

    private void layoutPanel() {
        refreshListButton.setIcon(AllIcons.Actions.Refresh);
        buildType.addItem(COURSE_LIST);
        buildType.addItem(COURSE_LINK);
        buildType.setSelectedItem(COURSE_LIST);

        setupDescriptionSettings(courseLinkDescription);
        setupDescriptionSettings(courseListDescription);
    }

    private void setupDescriptionSettings(JTextPane jTextPane) {
        jTextPane.setEditable(false);
        jTextPane.setContentType("text/html");
        jTextPane.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            protected void hyperlinkActivated(final HyperlinkEvent e) {
                BrowserUtil.browse(e.getURL());
            }
        });
    }

    private void initListeners() {
        buildType.addItemListener(new BuildTypeListener());
        refreshListButton.addActionListener(new RefreshActionListener());
        courseLinkFiled.addActionListener(new CourseLinkListener());
        courseListComboBox.addItemListener(new CourseListComboBoxListener());
    }

    private void setupGeneralSettings() {
        refreshCourseList(false);
        userName.setText(StepikConnectorLogin.getCurrentUserFullName());
    }

    Course getSelectedCourse() {
        return selectedCourse;
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    String getBuildType() {
        return (String) buildType.getSelectedItem();
    }

    private void refreshCourseList(boolean force) {
        courseListDescription.setText("");
        final List<Course> courses;
        courses = StepikProjectGenerator.getCoursesUnderProgress(force, project);

        courseListComboBox.removeAllItems();
        addCoursesToComboBox(courses);
        selectedCourse = courseListComboBox.getItemAt(0);
        if (selectedCourse == null) {
            selectedCourse = StepikProjectGenerator.EMPTY_COURSE;
        }
        courseListDescription.setText(selectedCourse.getDescription());
    }

    private void addCoursesToComboBox(@NotNull List<Course> courses) {
        courses.forEach(courseListComboBox::addItem);
        if (courseListComboBox.getItemCount() > 0) {
            courseListComboBox.setSelectedIndex(0);
        }
    }

    ValidationResult check() {
        if (selectedCourse.isAdaptive()) {
            return adaptiveCourse;
        }
        if (selectedCourse == StepikProjectGenerator.EMPTY_COURSE) {
            return invalidCourse;
        }
        return ValidationResult.OK;
    }

    /**
     * Listeners
     */
    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshCourseList(true);
            generator.fireStateChanged();
        }
    }

    private class BuildTypeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = e.getItem().toString();
                if (COURSE_LIST.equals(item)) {
                    ((CardLayout) courseSelectPanel.getLayout()).show(courseSelectPanel, COURSE_LIST);
                    selectedCourse = (Course) courseListComboBox.getSelectedItem();
                } else if (COURSE_LINK.equals(item)) {
                    ((CardLayout) courseSelectPanel.getLayout()).show(courseSelectPanel, COURSE_LINK);
                    selectedCourse = courseFromLink;
                }
                generator.fireStateChanged();
            }
        }
    }

    private class CourseListComboBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedCourse = (Course) e.getItem();
                courseListDescription.setText(selectedCourse.getDescription());
                generator.fireStateChanged();
            }
        }
    }

    private class CourseLinkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String link = courseLinkFiled.getText();
            int courseId = Utils.getCourseIdFromLink(link);

            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();
            Courses courses = null;

            if (courseId != 0) {
                courses = stepikApiClient.courses()
                        .get()
                        .id(courseId)
                        .execute();
            }

            if (courseId == 0 || courses.isEmpty()) {
                courseLinkDescription.setText("Wrong link");
                courseFromLink = StepikProjectGenerator.EMPTY_COURSE;
                selectedCourse = StepikProjectGenerator.EMPTY_COURSE;
                generator.fireStateChanged();
                return;
            }

            selectedCourse = courses.getCourses().get(0);
            courseFromLink = selectedCourse;
            courseLinkDescription.setText(String.format("<b>Course:</b> %s<br><br>%s",
                    selectedCourse.toString(), selectedCourse.getDescription()));
            generator.fireStateChanged();
        }
    }
}