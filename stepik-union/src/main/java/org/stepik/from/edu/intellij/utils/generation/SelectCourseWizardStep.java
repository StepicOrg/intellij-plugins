package org.stepik.from.edu.intellij.utils.generation;

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
import com.jetbrains.tmp.learning.SupportedLanguages;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;

public class SelectCourseWizardStep extends ModuleWizardStep {
    private static final Logger logger = Logger.getInstance(SelectCourseWizardStep.class);
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

    public SelectCourseWizardStep(
            @NotNull final StepikProjectGenerator generator,
            WizardContext wizardContext) {
        this.myGenerator = generator;
        project = wizardContext.getProject() == null ?
                DefaultProjectFactory.getInstance().getDefaultProject() :
                wizardContext.getProject();

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

        myAvailableCourses = myGenerator.getCoursesUnderProgress(false,
                "Getting Available Courses",
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
                        ProgressManager.getInstance().getProgressIndicator()
                                .setIndeterminate(true);
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

            StepikWrappers.CoursesContainer coursesContainer;
            if ("-1".equals(courseId) ||
                    (coursesContainer = StepikConnectorGet.getCourseInfos(courseId)) == null) {
                courseDescription.setText("Wrong link");
                return;
            }

            selectedCourse = coursesContainer.courses.get(0);
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

    @Override
    public void onStepLeaving() {
        String selectedLang = ((SupportedLanguages) langComboBox.getSelectedItem()).getName();
//        StudyTaskManager.getInstance(project).setDefaultLang(selectedLang);
        myGenerator.setDefaultLang(selectedLang);
        if (selectedCourse != null) {
            myGenerator.setSelectedCourse(selectedCourse);
        }
    }

    @Override
    public void onWizardFinished() throws CommitStepException {
        super.onWizardFinished();
        if (buildType.getSelectedItem().equals(COURSE_LINK)) {
            StepikConnectorPost.enrollToCourse(selectedCourse.getId());
        }
    }
}
