package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.HyperlinkAdapter;
import com.intellij.ui.PanelWithAnchor;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.CourseInfo;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikWrappers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

public class PyCourseCreatorSettingPanel extends JPanel implements PanelWithAnchor {
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
    private JTextField courseLinkFiled;
    private JTextPane courseListDescription;

    private JPanel courseListPanel;
    private JLabel courseListLabel;
    private JComboBox<CourseInfo> courseListComboBox;
    private JButton refreshListButton;
    private JTextPane courseLinkDescription;

    private final StepikProjectGenerator generator;
    private CourseInfo selectedCourse;
    private Project project;
    private List<CourseInfo> myAvailableCourses;

    private boolean isInit = false;

    PyCourseCreatorSettingPanel(
            @NotNull final StepikProjectGenerator generator) {
        this.generator = generator;
    }

    void init(Project project) {
        if (!isInit) {
            this.project = project;
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
//        checkCourseLinkButton.addActionListener(new CheckCourseLinkListener());
        courseLinkFiled.addActionListener(new CourseLinkListener());
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
        courseListDescription.setText(selectedCourse.getDescription());
    }

    @Override
    public JComponent getAnchor() {
        return mainPanel;
    }

    @Override
    public void setAnchor(@Nullable JComponent jComponent) {
    }

    CourseInfo getSelectedCourse() {
        return selectedCourse;
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    boolean validateCoursePanel() {
        generator.setDefaultLang(SupportedLanguages.PYTHON);
        if (selectedCourse == null || selectedCourse == CourseInfo.INVALID_COURSE) {
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

    /**
    *  Listeners
    */

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
                    ((CardLayout) courseSelectPanel.getLayout()).show(courseSelectPanel, COURSE_LIST);
                } else if (COURSE_LINK.equals(item)) {
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

    private class CourseLinkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String link = courseLinkFiled.getText();
            String courseId = getCourseIdFromLink(link);

            StepikWrappers.CoursesContainer coursesContainer;
            if ("-1".equals(courseId) ||
                    (coursesContainer = StepikConnectorGet.getCourseInfos(courseId)) == null) {
                courseLinkDescription.setText("Wrong link");
                return;
            }

            selectedCourse = coursesContainer.courses.get(0);
            generator.setSelectedCourse(selectedCourse);
            courseLinkDescription.setText(String.format("<b>Course:</b> %s<br><br>%s",
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
}