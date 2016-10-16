package com.jetbrains.tmp.utils.generation;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DefaultProjectFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.HyperlinkAdapter;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.stepik.CourseInfo;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import com.jetbrains.tmp.learning.stepik.StepikConnectorPost;
import com.jetbrains.tmp.learning.stepik.StepikWrappers;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.collective.SupportedLanguages;

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
    private JComboBox<String> buildType;

    private JPanel myCardPanel;

    private JPanel courseSelecter;
    private JLabel courseLabel;
    private JComboBox<CourseInfo> courseComboBox;

    private JButton refreshButton;
    private JPanel courseLinker;
    private JLabel courseLinkLabel;
    private JTextField courseLinkFiled;
    private JButton checkCourseLinkButton;
    private JLabel langLabel;
    private JComboBox<SupportedLanguages> langComboBox;

    private final StepikProjectGenerator myGenerator;
    private CourseInfo selectedCourse;
    private final Project project;
    private List<CourseInfo> myAvailableCourses;

    public SelectCourseWizardStep(@NotNull final StepikProjectGenerator generator,
                                  WizardContext wizardContext) {
        this.myGenerator = generator;
        project = wizardContext.getProject() == null ?
                DefaultProjectFactory.getInstance().getDefaultProject() : wizardContext.getProject();

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
        StepikConnectorLogin.loginFromDialog(project);
        userName.setText(StudyTaskManager.getInstance(project).getUser().getName());

        myAvailableCourses = myGenerator.getCoursesUnderProgress(false, "Getting Available Courses",
                ProjectManager.getInstance().getDefaultProject());
        myAvailableCourses.forEach(courseComboBox::addItem);

        selectedCourse = StudyUtils.getFirst(myAvailableCourses);
        myGenerator.setSelectedCourse(selectedCourse);
        courseDescription.setText(selectedCourse.getDescription());

        langComboBox.setModel(new EnumComboBoxModel<>(SupportedLanguages.class));
        langComboBox.setSelectedItem(SupportedLanguages.JAVA);
    }

    @Override
    public void updateDataModel() {

    }

    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            courseDescription.setText("");
            final java.util.List<CourseInfo> courses = ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(() -> {
                        ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                        return myGenerator.getCourses(true);
                    }, "Refreshing Course List", true, project);

            if (!courses.contains(CourseInfo.INVALID_COURSE)) {
                refreshCoursesList(courses);
            }
        }

        private void refreshCoursesList(@NotNull final java.util.List<CourseInfo> courses) {
            if (courses.isEmpty()) {
                return;
            }
            courseComboBox.removeAllItems();

            addCoursesToCombobox(courses);
            selectedCourse = StudyUtils.getFirst(courses);
            myGenerator.setSelectedCourse(selectedCourse);
            courseDescription.setText(selectedCourse.getDescription());

            myGenerator.setCourses(courses);
            myAvailableCourses = courses;
            StepikProjectGenerator.flushCache(courses);
        }

        private void addCoursesToCombobox(@NotNull java.util.List<CourseInfo> courses) {
            for (CourseInfo courseInfo : courses) {
                courseComboBox.addItem(courseInfo);
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

            selectedCourse = StepikConnectorGet.getCourseInfos(courseId).courses.get(0);
            myGenerator.setSelectedCourse(selectedCourse);
            courseDescription.setText(String.format("<b>Course:</b> %s<br><br>%s",
                    selectedCourse.toString(), selectedCourse.getDescription()));
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

            if (link.contains("course/")) {
                List<String> ar = Arrays.asList(link.split("/"));
                int i = ar.indexOf("course");
                if (i == -1) {
                    return "-1";
                }
                String[] parts = ar.get(i + 1).split("-");
                return parts[parts.length - 1];
            }
            if (link.contains("unit=")) {
                String unitId = link.split("&")[1].split("=")[1];
                List<StepikWrappers.Unit> units = StepikConnectorGet.getUnits(unitId).units;
                String sectionId = Integer.toString(units.get(0).section);
                return Integer.toString(StepikConnectorGet.getSections(sectionId).sections.get(0).course);
            }
            if (link.contains("lesson/")) {
                List<String> ar = Arrays.asList(link.split("/"));
                int i = ar.indexOf("lesson");
                if (i == -1) {
                    return "-1";
                }
                String[] parts = ar.get(i + 1).split("-");
                String lessonId = parts[parts.length - 1];
                List<StepikWrappers.Unit> units = StepikConnectorGet.getUnits("?lesson=" + lessonId).units;
                String sectionId = Integer.toString(units.get(0).section);
                return Integer.toString(StepikConnectorGet.getSections(sectionId).sections.get(0).course);
            }
            return "-1";
        }

        private boolean isFillOfInt(@NotNull String link) {
            return !link.matches("[^0-9]");
        }
    }

    @Override
    public void onStepLeaving() {
        String selLang = ((SupportedLanguages) langComboBox.getSelectedItem()).getName();
        StudyTaskManager.getInstance(project).setDefaultLang(selLang);
        if (selectedCourse != null){
            myGenerator.setSelectedCourse(selectedCourse);
        }
    }

    @Override
    public void onWizardFinished() throws CommitStepException {
        super.onWizardFinished();
        if (buildType.getSelectedItem().equals(COURSE_LINK)){
            StepikConnectorPost.enrollToCourse(selectedCourse.getId());
        }
    }
}
