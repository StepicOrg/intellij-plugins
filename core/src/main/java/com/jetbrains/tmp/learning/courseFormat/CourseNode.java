package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.users.User;
import org.stepik.api.objects.users.Users;

import java.util.ArrayList;
import java.util.List;

public class CourseNode implements StudyNode {
    private static final Logger logger = Logger.getInstance(CourseNode.class);
    private Course data;
    private List<User> authors;
    private List<SectionNode> sectionNodes;

    public CourseNode() {
    }

    public CourseNode(@NotNull Course data) {
        this.data = data;
        init(true, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseNode that = (CourseNode) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        //noinspection SimplifiableIfStatement
        if (authors != null ? !authors.equals(that.authors) : that.authors != null) return false;
        return sectionNodes != null ? sectionNodes.equals(that.sectionNodes) : that.sectionNodes == null;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (authors != null ? authors.hashCode() : 0);
        result = 31 * result + (sectionNodes != null ? sectionNodes.hashCode() : 0);
        return result;
    }

    public void init(boolean isRestarted, @Nullable ProgressIndicator indicator) {
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
            if (indicator != null) {
                indicator.setText("Refresh " + getName());
                indicator.setText2("Update course authors");
            }
            List<Long> authorsIds = data.getAuthors();
            if (authorsIds.size() > 0) {
                Users users = stepikApiClient.users()
                        .get()
                        .id(authorsIds)
                        .execute();
                setAuthors(users.getUsers());
            }

            if (indicator != null) {
                indicator.setText2("Update sections");
            }
            List<Long> sectionsIds = data.getSections();
            if (sectionsIds.size() > 0) {
                Sections sections = stepikApiClient.sections()
                        .get()
                        .id(sectionsIds)
                        .execute();


                for (Section section : sections.getSections()) {
                    SectionNode sectionNode = getSectionById(section.getId());
                    if (sectionNode != null) {
                        sectionNode.setData(section);
                    } else {
                        SectionNode item = new SectionNode(this, section);
                        if (item.getLessonNodes().size() > 0) {
                            getSectionNodes().add(item);
                        }
                    }
                }
            }
        } catch (StepikClientException logged) {
            logger.warn("A course initialization don't is fully", logged);
        }

        for (SectionNode sectionNode : getSectionNodes()) {
            sectionNode.init(this, isRestarted, indicator);
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

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setAuthors(@Nullable List<User> authors) {
        this.authors = authors;
    }

    @Transient
    @NotNull
    @Override
    public String getName() {
        return getData().getTitle();
    }

    @Transient
    @Override
    public int getPosition() {
        return 0;
    }

    @Transient
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

    @Nullable
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

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public Course getData() {
        if (data == null) {
            data = new Course();
        }
        return data;
    }

    @SuppressWarnings("unused")
    public void setData(@Nullable Course data) {
        this.data = data;
    }
}
