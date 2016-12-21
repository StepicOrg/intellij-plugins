package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.HyperlinkAdapter;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.CourseInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class PyCourseCreatorSettingPanel extends JPanel {
    private static final Logger logger = Logger.getInstance(PyCourseCreatorSettingPanel.class);
    private final static String COURSE_LIST = "Course list";
    final static String COURSE_LINK = "Course link";

    private JPanel mainPanel;
    private JLabel nameLabel;
    private JLabel userName;
    private JLabel buildLabel;
    private JComboBox<String> buildType;

    private JPanel courseSelectPanel;

    private JPanel courseLinkPanel;
    private JLabel courseLinkLabel;
    private JComboBox<CourseInfo> courseListComboBox;
    private JTextField courseLinkFiled;
    private JTextPane courseListDescription;

    private JPanel courseListPanel;
    private JLabel courseListLabel;
    private JButton refreshListButton;
    private JTextPane courseLinkDescription;

    private final StepikProjectGenerator generator;
    private CourseInfo selectedCourse;
    private Project project;
    private List<CourseInfo> myAvailableCourses;
    private FacetValidatorsManager validationManager;

    PyCourseCreatorSettingPanel(
            @NotNull final StepikProjectGenerator generator) {
        this.generator = generator;
    }

    void init(Project project) {
        this.project = project;
        layoutPanel();
        initListeners();
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
//        checkCourseLinkButton.addActionListener(new CheckCourseLinkListener());
        courseListComboBox.addItemListener(new CourseListComboBoxListener());
    }

    private void setupGeneralSettings() {
        userName.setText(StudyTaskManager.getInstance(project).getUser().getName());

        myAvailableCourses = generator.getCoursesUnderProgress(
                false,
                "Getting Available Courses",
                ProjectManager.getInstance().getDefaultProject());
        myAvailableCourses.forEach(courseListComboBox::addItem);

        selectedCourse = StudyUtils.getFirst(myAvailableCourses);
        generator.setSelectedCourse(selectedCourse);
        courseLinkDescription.setText(selectedCourse.getDescription());
    }

    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            courseLinkDescription.setText("");
            final List<CourseInfo> courses =
                    generator.getCoursesUnderProgress(true,
                            "Refreshing Course List",
                            project);

            if (!courses.contains(CourseInfo.INVALID_COURSE)) {
                refreshCoursesList(courses);
            }
        }

        private void refreshCoursesList(@NotNull final List<CourseInfo> courses) {
            if (courses.isEmpty()) {
                return;
            }
            courseListComboBox.removeAllItems();

            addCoursesToCombobox(courses);
            selectedCourse = StudyUtils.getFirst(courses);
            generator.setSelectedCourse(selectedCourse);
            courseLinkDescription.setText(selectedCourse.getDescription());

            generator.setCourses(courses);
            myAvailableCourses = courses;
            StepikProjectGenerator.flushCache(courses);
        }

        private void addCoursesToCombobox(@NotNull List<CourseInfo> courses) {
            for (CourseInfo courseInfo : courses) {
                courseListComboBox.addItem(courseInfo);
            }
        }
    }

    private class BuildTypeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = e.getItem().toString();
                if (COURSE_LIST.equals(item)) {
                    courseLinkDescription.setText("");
                    ((CardLayout) courseSelectPanel.getLayout()).show(courseSelectPanel, COURSE_LIST);
                } else if (COURSE_LINK.equals(item)) {
                    courseLinkDescription.setText("");
                    ((CardLayout) courseSelectPanel.getLayout()).show(courseSelectPanel, COURSE_LINK);
                }
            }
        }
    }

    private class CourseListComboBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedCourse = (CourseInfo) e.getItem();
                courseListDescription.setText(selectedCourse.getDescription());
            }
        }
    }

    CourseInfo getSelectedCourse() {
        return selectedCourse;
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    boolean validateCoursePanel(){
        generator.setDefaultLang(SupportedLanguages.PYTHON);
        if (selectedCourse == null|| selectedCourse == CourseInfo.INVALID_COURSE ) {
            return false;
        }
        generator.setSelectedCourse(selectedCourse);
        return true;
    }

    public JComboBox<CourseInfo> getCourseListComboBox() {
        return courseListComboBox;
    }

    String getBuildType() {
        return (String) buildType.getSelectedItem();
    }
}