package org.stepik.plugin.projectWizard.idea;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.HyperlinkAdapter;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
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

class JavaCCSettingsPanel extends ModuleWizardStep {
    private static final Logger logger = Logger.getInstance(JavaCCSettingsPanel.class);
    private final static String COURSE_LIST = "Course list";
    private final static String COURSE_LINK = "Course link";
    private final StepikProjectGenerator projectGenerator;
    private final Project project;
    private JPanel mainPanel;
    private JLabel nameLabel;
    private JLabel userName;
    private JLabel langLabel;
    private JComboBox<SupportedLanguages> langComboBox;
    private JLabel buildLabel;
    private JComboBox<String> buildType;
    private JPanel courseSelectPanel;
    private JPanel courseListPanel;
    private JLabel courseListLabel;
    private JComboBox<Course> courseListComboBox;
    private JButton refreshListButton;
    private JTextPane courseListDescription;
    private JPanel courseLinkPanel;
    private JLabel courseLinkLabel;
    private JTextField courseLinkFiled;
    private JTextPane courseLinkDescription;
    @NotNull
    private Course selectedCourse = StepikProjectGenerator.EMPTY_COURSE;
    @NotNull
    private Course courseFromLink = StepikProjectGenerator.EMPTY_COURSE;

    JavaCCSettingsPanel(
            @NotNull final StepikProjectGenerator projectGenerator,
            @NotNull Project project) {
        this.projectGenerator = projectGenerator;
        this.project = project;

        layoutPanel();
        initListeners();
    }

    private void layoutPanel() {
        refreshListButton.setIcon(AllIcons.Actions.Refresh);
        buildType.addItem(COURSE_LIST);
        buildType.addItem(COURSE_LINK);
        buildType.setSelectedItem(COURSE_LIST);

        setupDescriptionSettings(courseListDescription);
        setupDescriptionSettings(courseLinkDescription);
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

    @Override
    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public void updateStep() {
        StepikConnectorLogin.loginFromDialog(project);
        String username = StepikConnectorLogin.getCurrentUserFullName();
        userName.setText(username);
        refreshCourseList(false);

        langComboBox.addItem(SupportedLanguages.PYTHON);
        langComboBox.addItem(SupportedLanguages.JAVA);
        langComboBox.setSelectedItem(SupportedLanguages.JAVA);
    }

    @Override
    public void updateDataModel() {
    }

    @Override
    public boolean validate() throws ConfigurationException {
        return !selectedCourse.isAdaptive() && selectedCourse != StepikProjectGenerator.EMPTY_COURSE;
    }

    @Override
    public void onStepLeaving() {
        SupportedLanguages selectedLang = (SupportedLanguages) langComboBox.getSelectedItem();
        projectGenerator.setDefaultLang(selectedLang);
        projectGenerator.setSelectedCourse(selectedCourse);
    }

    @Override
    public void onWizardFinished() throws CommitStepException {
        super.onWizardFinished();
        int id = selectedCourse.getId();
        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();
        try {
            stepikApiClient.enrollments()
                    .post()
                    .course(id)
                    .execute();
        } catch (StepikClientException e) {
            String message = String.format("Can't enrollment on a course: id = %s, name = %s",
                    id, selectedCourse.getTitle());
            logger.error(message, e);
            throw new CommitStepException(message);
        }
        logger.info(String.format("Finished the project wizard with the selected course: id = %s, name = %s",
                id, selectedCourse.getTitle()));
        StepikProjectGenerator.downloadAndFlushCourse(project, selectedCourse.getId());
    }

    private void refreshCourseList(boolean force) {
        courseListDescription.setText("");
        final List<Course> courses;
        courses = StepikProjectGenerator.getCoursesUnderProgress(force, project);

        courseListComboBox.removeAllItems();
        addCoursesToComboBox(courses);

        if (courseListComboBox.getItemAt(0) == null) {
            selectedCourse = StepikProjectGenerator.EMPTY_COURSE;
        } else {
            selectedCourse = courseListComboBox.getItemAt(0);
        }
        courseListDescription.setText(selectedCourse.getDescription());
    }

    private void addCoursesToComboBox(@NotNull List<Course> courses) {
        courses.forEach(courseListComboBox::addItem);
        if (courseListComboBox.getItemCount() > 0) {
            courseListComboBox.setSelectedIndex(0);
        }
    }

    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshCourseList(true);
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
            }
        }
    }

    private class CourseListComboBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedCourse = (Course) e.getItem();
                courseListDescription.setText(selectedCourse.getDescription());
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
                return;
            }

            selectedCourse = courses.getCourses().get(0);
            courseFromLink = selectedCourse;

            StringBuilder sb = new StringBuilder();
            sb.append("<b>Course:</b> ")
                    .append(selectedCourse.toString())
                    .append("<br><br>")
                    .append(selectedCourse.getDescription());
            if (selectedCourse.isAdaptive()) {
                sb.append("<p style='font-weight: bold;'>This course is adaptive.<br>")
                        .append("Sorry, but we don't support adaptive courses yet</p>");
            }
            courseLinkDescription.setText(sb.toString());
        }
    }
}