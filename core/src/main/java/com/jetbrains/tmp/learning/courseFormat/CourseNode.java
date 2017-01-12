package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.users.User;
import org.stepik.api.objects.users.Users;

import java.util.ArrayList;
import java.util.List;

public class CourseNode implements StudyNode {
    private Course data;
    @Nullable
    private List<User> authors;
    @Nullable
    private List<SectionNode> sectionNodes;

    public CourseNode() {
    }

    public CourseNode(Course data) {
        this.data = data;

        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        List<Long> authorsIds = data.getAuthors();
        Users users = stepikApiClient.users()
                .get()
                .id(authorsIds)
                .execute();

        setAuthors(users.getUsers());

        List<Long> sectionsIds = data.getSections();
        Sections sections = stepikApiClient.sections()
                .get()
                .id(sectionsIds)
                .execute();

        ArrayList<SectionNode> sectionsList = new ArrayList<>();
        for (Section section : sections.getSections()) {
            SectionNode item = new SectionNode(section);
            if (item.getLessonNodes().size() > 0) {
                sectionsList.add(item);
            }
        }

        setSectionNodes(sectionsList);

        initCourse(true);
    }

    /**
     * Initializes state of course
     */
    public void initCourse(boolean isRestarted) {
        for (SectionNode sectionNode : getSectionNodes()) {
            sectionNode.initSection(this, isRestarted);
        }
    }

    @SuppressWarnings("unused")
    @NotNull
    public List<User> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public void setAuthors(@Nullable List<User> authors) {
        this.authors = authors;
    }

    @NotNull
    @Override
    public String getName() {
        return getData().getTitle();
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public long getId() {
        return getData().getId();
    }

    void addSection(@NotNull SectionNode sectionNode) {
        getSectionNodes().add(sectionNode);
        getSectionNodes().sort(StudyNodeComparator.getInstance());
    }

    @Nullable
    public SectionNode getSectionById(long id) {
        for (SectionNode sectionNode : getSectionNodes()) {
            if (sectionNode.getId() == id) {
                return sectionNode;
            }
        }
        return null;
    }

    @Nullable
    public LessonNode getLessonById(long id) {
        for (SectionNode sectionNode : getSectionNodes()) {
            for (LessonNode lessonNode : sectionNode.getLessonNodes()) {
                if (lessonNode.getId() == id) {
                    return lessonNode;
                }
            }
        }
        return null;
    }

    @Nullable
    public StepNode getStepById(long id) {
        for (SectionNode sectionNode : getSectionNodes()) {
            for (LessonNode lessonNode : sectionNode.getLessonNodes()) {
                for (StepNode stepNode : lessonNode.getStepNodes()) {
                    if (stepNode.getId() == id) {
                        return stepNode;
                    }
                }
            }
        }
        return null;
    }

    @NotNull
    public List<SectionNode> getSectionNodes() {
        if (sectionNodes == null) {
            sectionNodes = new ArrayList<>();
        }
        return sectionNodes;
    }

    @SuppressWarnings("unused")
    public void setSectionNodes(@Nullable List<SectionNode> sectionNodes) {
        this.sectionNodes = sectionNodes;
    }

    @Nullable
    public SectionNode getSectionByDirName(@NotNull String dirName) {
        int id = EduUtils.parseDirName(dirName, EduNames.SECTION);
        return getSectionById(id);
    }

    public LessonNode getLessonByDirName(@NotNull String name) {
        int id = EduUtils.parseDirName(name, EduNames.LESSON);
        return getLessonById(id);
    }

    @Transient
    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (SectionNode sectionNode : getSectionNodes()) {
            if (sectionNode.getStatus() != StudyStatus.SOLVED)
                return StudyStatus.UNCHECKED;
        }

        return StudyStatus.SOLVED;
    }

    @Transient
    @NotNull
    @Override
    public String getDirectory() {
        return "";
    }

    @Transient
    @NotNull
    @Override
    public String getPath() {
        return "";
    }

    @Transient
    @Nullable
    public SectionNode getPrevSection(@NotNull SectionNode sectionNode) {
        int position = sectionNode.getPosition();
        List<SectionNode> children = getSectionNodes();
        for (int i = children.size() - 1; i >= 0; i--) {
            SectionNode item = children.get(i);
            if (item.getPosition() < position) {
                return item;
            }
        }
        return null;
    }

    @Transient
    @Nullable
    public SectionNode getNextSection(@NotNull SectionNode sectionNode) {
        int position = sectionNode.getPosition();
        for (SectionNode item : getSectionNodes()) {
            if (item.getPosition() > position) {
                return item;
            }
        }
        return null;
    }

    public Course getData() {
        if (data == null) {
            data = new Course();
        }
        return data;
    }

    public void setData(Course data) {
        this.data = data;
    }
}
