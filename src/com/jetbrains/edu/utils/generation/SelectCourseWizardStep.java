package com.jetbrains.edu.utils.generation;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DefaultProjectFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.HyperlinkAdapter;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.StudyUtils;
import com.jetbrains.edu.learning.stepic.CourseInfo;
import com.jetbrains.edu.learning.stepic.StepicConnectorGet;
import com.jetbrains.edu.learning.stepic.StepicConnectorLogin;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;

public class SelectCourseWizardStep extends ModuleWizardStep {
    private static final Logger LOG = Logger.getInstance(SelectCourseWizardStep.class);
    private final static String COURSE_LIST = "Course list";
    private final static String COURSE_LINK = "Course link";

    private JPanel mainPanel;
    private JLabel nameLabel;
    private JLabel userName;
    private JLabel buildLabel;
    private JTextPane courseDescription;
    private JComboBox buildType;

    private JPanel myCardPanel;

    private JPanel courseSelecter;
    private JLabel courseLabel;
    private JComboBox<CourseInfo> courseComboBox;

    private JButton refreshButton;
    private JPanel courseLinker;
    private JLabel courseLinkLabel;
    private JTextField courseLinkFiled;
    private JButton checkCourseLinkButton;

    private final StepikProjectGenerator myGenerator;
    private final WizardContext wizardContext;
    private CourseInfo selectedCourse;
    Project defaultProject = DefaultProjectFactory.getInstance().getDefaultProject();
//    private final Project project;

    public SelectCourseWizardStep(@NotNull final StepikProjectGenerator generator, WizardContext wizardContext) {
        this.myGenerator = generator;
        this.wizardContext = wizardContext;


        layoutPanel();
        initListeners();
    }

    private void layoutPanel() {
        refreshButton.setIcon(AllIcons.Actions.Refresh);

        buildType.addItem(COURSE_LIST);
        buildType.addItem(COURSE_LINK);
        buildType.setSelectedItem(COURSE_LIST);

        courseDescription.setEditable(false);
        courseDescription.setContentType("text/html");
        courseDescription.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            protected void hyperlinkActivated(final HyperlinkEvent e) {
                BrowserUtil.browse(e.getURL());
            }
        });

        List<CourseInfo> myAvailableCourses = myGenerator.getCoursesUnderProgress(false, "Getting Available Courses", ProjectManager.getInstance().getDefaultProject());
        myAvailableCourses.forEach(courseComboBox::addItem);
    }

    private void initListeners() {
        buildType.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = e.getItem().toString();
                if (COURSE_LIST.equals(item)) {
                    courseDescription.setText("");
                    ((CardLayout) myCardPanel.getLayout()).show(myCardPanel, COURSE_LIST);
                } else if (COURSE_LINK.equals(item)) {
                    courseDescription.setText("");
                    ((CardLayout) myCardPanel.getLayout()).show(myCardPanel, COURSE_LINK);
                }
//                erasePassword();
            }
        });

        refreshButton.addActionListener(new RefreshActionListener());
        checkCourseLinkButton.addActionListener(new CheckCourseLinkListener());

        courseComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedCourse = (CourseInfo) e.getItem();
                courseDescription.setText(selectedCourse.getDescription());
            }
        });
    }

    @Override
    public JComponent getComponent() {

        return mainPanel;
    }

    @Override
    public void updateStep() {
        StepicConnectorLogin.resetClient();
        StepicConnectorLogin.loginFromDialog(defaultProject);
        userName.setText(StudyTaskManager.getInstance(defaultProject).getUser().getName());
    }

    @Override
    public void updateDataModel() {

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            courseDescription.setText("");
            final java.util.List<CourseInfo> courses = ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(() -> {
                        ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                        return myGenerator.getCourses(true);
                    }, "Refreshing Course List", true, defaultProject);

            if (!courses.contains(CourseInfo.INVALID_COURSE)) {
                refreshCoursesList(courses);
            }
        }

        private void refreshCoursesList(@NotNull final java.util.List<CourseInfo> courses) {
            if (courses.isEmpty()) {
//                setError(CONNECTION_ERROR);
                return;
            }
            courseComboBox.removeAllItems();

            addCoursesToCombobox(courses);
            selectedCourse = StudyUtils.getFirst(courses);
            myGenerator.setSelectedCourse(selectedCourse);
            courseDescription.setText(selectedCourse.getDescription());

            myGenerator.setCourses(courses);
//            myAvailableCourses = courses;
            myGenerator.flushCache(courses);
        }

        private void addCoursesToCombobox(@NotNull java.util.List<CourseInfo> courses) {
            for (CourseInfo courseInfo : courses) {
                courseComboBox.addItem(courseInfo);
//                LOG.warn(courseInfo.toString());
            }
        }
    }

    private class CheckCourseLinkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String link = courseLinkFiled.getText();
            String courseId = getCourseIdFromLink(link);
            if ("-1".equals(courseId)) {
                courseDescription.setText("Wrong link");
                return;
            }

            selectedCourse = StepicConnectorGet.getCourseInfos(courseId).courses.get(0);
            myGenerator.setSelectedCourse(selectedCourse);
            courseDescription.setText("<b>Course:</b> " + selectedCourse.toString() + "<br><br>" + selectedCourse.getDescription());
        }

        private String getCourseIdFromLink(String link) {
            link = link.trim();
            if (link.isEmpty()) return "-1";
            if (isFillOfInt(link)) {
                return link;
            }

            if (link.contains("course/")) {
                List<String> ar = Arrays.asList(link.split("/"));
                int i = 0;
                while (i < ar.size() && !ar.get(i++).equals("course")) ;
                if (i == ar.size()) return "-1";
                String[] parts = ar.get(i).split("-");
                return parts[parts.length - 1];
            }
            if (link.contains("unit=")) {
                String unitId = link.split("&")[1].split("=")[1];
                String sectionId = Integer.toString(StepicConnectorGet.getUnits(unitId).units.get(0).section);
                return Integer.toString(StepicConnectorGet.getSections(sectionId).sections.get(0).course);
            }
            if (link.contains("lesson/")) {
                List<String> ar = Arrays.asList(link.split("/"));
                int i = 0;
                while (i < ar.size() && !ar.get(i++).equals("lesson")) ;
                if (i == ar.size()) return "-1";
                String[] parts = ar.get(i).split("-");
                String lessonId = parts[parts.length - 1];
                String sectionId = Integer.toString(StepicConnectorGet.getUnits("?lesson=" + lessonId).units.get(0).section);
                return Integer.toString(StepicConnectorGet.getSections(sectionId).sections.get(0).course);
            }
            return "-1";
        }

        private boolean isFillOfInt(String link) {
            for (int i = 0; i < link.length(); i++) {
                if (!Character.isDigit(link.charAt(i)))
                    return false;
            }
            return true;
        }
    }

    @Override
    public void onStepLeaving() {
        if (selectedCourse != null)
            myGenerator.setSelectedCourse(selectedCourse);
    }
}
