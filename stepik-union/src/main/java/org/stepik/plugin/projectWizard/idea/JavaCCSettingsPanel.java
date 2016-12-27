package org.stepik.plugin.projectWizard.idea;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.HyperlinkAdapter;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.CourseInfo;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import com.jetbrains.tmp.learning.stepik.StepikConnectorPost;
import com.jetbrains.tmp.learning.stepik.StepikWrappers;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
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
    private JComboBox<CourseInfo> courseListComboBox;
    private JButton refreshListButton;
    private JTextPane courseListDescription;
    private JPanel courseLinkPanel;
    private JLabel courseLinkLabel;
    private JTextField courseLinkFiled;
    private JTextPane courseLinkDescription;
    @NotNull
    private CourseInfo selectedCourse = CourseInfo.INVALID_COURSE;
    @NotNull
    private CourseInfo courseFromLink = CourseInfo.INVALID_COURSE;

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
        userName.setText(StepikProjectManager.getInstance(project).getUser().getName());
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
        return !selectedCourse.isAdaptive() && selectedCourse != CourseInfo.INVALID_COURSE;
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
        StepikConnectorPost.enrollToCourse(id);
        logger.info(String.format("Finished the project wizard with the selected course: id = %s, name = %s",
                id, selectedCourse.getName()));
        StepikProjectGenerator.downloadAndFlushCourse(project, selectedCourse);
    }

    private void refreshCourseList(boolean force) {
        courseListDescription.setText("");
        final List<CourseInfo> courses =
                StepikProjectGenerator.getCoursesUnderProgress(force,
                        project);

        courseListComboBox.removeAllItems();
        addCoursesToComboBox(courses);

        if (courseListComboBox.getItemAt(0) == null) {
            selectedCourse = CourseInfo.INVALID_COURSE;
        } else {
            selectedCourse = courseListComboBox.getItemAt(0);
        }
        courseListDescription.setText(selectedCourse.getDescription());
    }

    private void addCoursesToComboBox(@NotNull List<CourseInfo> courses) {
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
                    selectedCourse = (CourseInfo) courseListComboBox.getSelectedItem();
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
                selectedCourse = (CourseInfo) e.getItem();
                courseListDescription.setText(selectedCourse.getDescription());
            }
        }
    }

    private class CourseLinkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String link = courseLinkFiled.getText();
            String courseId = getCourseIdFromLink(link);

            StepikWrappers.CoursesContainer coursesContainer;
            if ("-1".equals(courseId) ||
                    (coursesContainer = StepikConnectorGet.getCourseInfos(courseId)) == null) {
                courseLinkDescription.setText("Wrong link");
                courseFromLink = CourseInfo.INVALID_COURSE;
                selectedCourse = CourseInfo.INVALID_COURSE;
                return;
            }

            selectedCourse = coursesContainer.courses.get(0);
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

        @NotNull
        private String getCourseIdFromLink(@NotNull String link) {
            link = link.trim();
            if (link.isEmpty()) {
                return "-1";
            }
            if (isFillOfInt(link)) {
                return link;
            }

            List<String> list = Arrays.asList(link.split("/"));
            int i = list.indexOf("course");
            if (i != -1) {
                if (i + 1 == list.size())
                    return "-1";
                String[] parts = list.get(i + 1).split("-");
                return parts.length != 0 ? parts[parts.length - 1] : "-1";
            }

            String[] paramStr = link.split("\\?");
            if (paramStr.length > 1) {
                String[] params = paramStr[1].split("&");
                final String[] unitId = {"-1"};
                Arrays.stream(params)
                        .filter(s -> s.startsWith("unit="))
                        .forEach(s -> unitId[0] = s.substring(5, s.length()));

                if (!unitId[0].equals("-1")) {
                    StepikWrappers.UnitContainer unitContainer =
                            StepikConnectorGet.getUnits(unitId[0]);
                    if (unitContainer == null) {
                        return "-1";
                    }
                    return getCourseId(unitContainer);
                }
            }

            list = Arrays.asList(link.split("/"));
            i = list.indexOf("lesson");
            if (i != -1) {
                if (i + 1 == list.size())
                    return "-1";
                String[] parts = list.get(i + 1).split("-");
                String lessonId = parts[parts.length - 1];
                StepikWrappers.UnitContainer unitContainer =
                        StepikConnectorGet.getUnits("?lesson=" + lessonId);

                return unitContainer == null ? "-1" : getCourseId(unitContainer);
            }
            return "-1";
        }

        @NotNull
        private String getCourseId(@NotNull StepikWrappers.UnitContainer unitContainer) {
            String sectionId = Integer.toString(unitContainer.units.get(0).section);
            StepikWrappers.SectionContainer sectionContainer =
                    StepikConnectorGet.getSections(sectionId);

            return sectionContainer == null ? "-1" :
                    Integer.toString(sectionContainer.sections.get(0).course);
        }

        private boolean isFillOfInt(@NotNull String link) {
            return link.matches("[0-9]+");
        }
    }
}