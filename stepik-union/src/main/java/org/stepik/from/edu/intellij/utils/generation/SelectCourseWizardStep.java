package org.stepik.from.edu.intellij.utils.generation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DefaultProjectFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.HyperlinkAdapter;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static com.jetbrains.tmp.learning.StudyUtils.execCancelable;

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

    private JPanel cardPanel;

    private JPanel courseSelector;
    private JLabel courseLabel;
    private JComboBox<CourseInfo> courseComboBox;

    private JButton refreshButton;
    private JPanel courseLinker;
    private JLabel courseLinkLabel;
    private JTextField courseLinkFiled;
    private JButton checkCourseLinkButton;
    private JLabel langLabel;
    private JComboBox<SupportedLanguages> langComboBox;

    private final StepikProjectGenerator projectGenerator;
    private CourseInfo selectedCourse;
    private final Project project;

    public SelectCourseWizardStep(
            @NotNull final StepikProjectGenerator generator,
            @NotNull WizardContext wizardContext) {
        this.projectGenerator = generator;
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
                    ((CardLayout) cardPanel.getLayout()).show(cardPanel, COURSE_LIST);
                } else if (COURSE_LINK.equals(item)) {
                    courseDescription.setText("");
                    ((CardLayout) cardPanel.getLayout()).show(cardPanel, COURSE_LINK);
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

        List<CourseInfo> courses = projectGenerator.getCoursesUnderProgress(false,
                "Getting Available Courses",
                ProjectManager.getInstance().getDefaultProject());
        addCoursesToComboBox(courses);
        projectGenerator.setSelectedCourse(selectedCourse);
        courseDescription.setText(selectedCourse.getDescription());

        //noinspection unchecked
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
                        return projectGenerator.getCourses(true);
                    }, "Refreshing Course List", true, project);

            if (!courses.contains(CourseInfo.INVALID_COURSE)) {
                refreshCoursesList(courses);
            }
        }

        private void refreshCoursesList(@NotNull final List<CourseInfo> courses) {
            if (courses.isEmpty()) {
                return;
            }
            courseComboBox.removeAllItems();
            addCoursesToComboBox(courses);
            projectGenerator.setSelectedCourse(selectedCourse);
            courseDescription.setText(selectedCourse.getDescription());
            projectGenerator.setCourses(courses);
            StepikProjectGenerator.flushCache(courses);
        }
    }

    private void addCoursesToComboBox(@NotNull java.util.List<CourseInfo> courses) {
        courses.stream()
                .filter(course -> !course.isAdaptive())
                .forEach(courseComboBox::addItem);
        if (courseComboBox.getItemCount() > 0) {
            courseComboBox.setSelectedIndex(0);
        }
        selectedCourse = courseComboBox.getItemAt(0);
    }

    @Override
    public boolean validate() throws ConfigurationException {
        return !selectedCourse.isAdaptive();
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
            courseComboBox.setSelectedItem(selectedCourse);
            projectGenerator.setSelectedCourse(selectedCourse);
            String description = String.format("<b>Course:</b> %s<br><br>%s",
                    selectedCourse.toString(), selectedCourse.getDescription());
            if (selectedCourse.isAdaptive()) {
                description += "<p style='font-weight: bold;'>This course is adaptive.<br>" +
                        "Sorry, but we don't support adaptive courses yet</p>";
            }
            courseDescription.setText(description);
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
        SupportedLanguages selectedLang = (SupportedLanguages) langComboBox.getSelectedItem();
        projectGenerator.setDefaultLang(selectedLang);
        if (selectedCourse != null) {
            projectGenerator.setSelectedCourse(selectedCourse);
        }
    }

    @Override
    public void onWizardFinished() throws CommitStepException {
        super.onWizardFinished();
        if (COURSE_LINK.equals(buildType.getSelectedItem())) {
            int id = selectedCourse.getId();
            StepikConnectorPost.enrollToCourse(id);
            logger.info(String.format("Finished the project wizard with the selected course: id = %s, name = %s",
                    id, selectedCourse.getName()));
        }
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
            return execCancelable(() -> {
                final Course course = StepikConnectorGet.getCourse(project, selectedCourse);
                if (course != null) {
                    flushCourse(project, course);
                    course.initCourse(false);
                }
                return course;
            });
        }, "Creating Course", true, project);
    }

    private static void flushCourse(@NotNull final Project project, @NotNull final Course course) {
        final File courseDirectory = StudyUtils.getCourseDirectory(project, course);
        FileUtil.createDirectory(courseDirectory);
        flushCourseJson(course, courseDirectory);
    }

    private static void flushCourseJson(@NotNull final Course course, @NotNull final File courseDirectory) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        final String json = gson.toJson(course);
        final File courseJson = new File(courseDirectory, EduNames.COURSE_META_FILE);
        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(courseJson);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            try {
                outputStreamWriter.write(json);
            } catch (IOException e) {
                Messages.showErrorDialog(e.getMessage(), "Failed to Generate Json");
                logger.info(e);
            } finally {
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    logger.info(e);
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.info(e);
        }
    }
}