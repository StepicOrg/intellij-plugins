package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
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
import java.util.Collections;
import java.util.List;

import static com.jetbrains.tmp.learning.stepik.StepikConnectorLogin.authAndGetStepikApiClient;

public class CourseNode extends Node<SectionNode, Course> {
    private static final Logger logger = Logger.getInstance(CourseNode.class);
    private Course data;
    private List<User> authors;
    private List<SectionNode> sectionNodes;

    public CourseNode() {
    }

    public CourseNode(@NotNull StudyNode parent, @NotNull Course data) {
        super(parent, data);
    }

    public CourseNode(@NotNull Course data, @Nullable ProgressIndicator indicator) {
        super(data, indicator);
    }

    protected void init(@Nullable StudyNode parent, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        try {
            StepikApiClient stepikApiClient = authAndGetStepikApiClient();
            if (indicator != null) {
                indicator.setText("Refresh " + getName());
                indicator.setText2("Update sections");
            }

            authors = null;

            List<Long> sectionsIds = getData().getSections();
            if (sectionsIds.size() > 0) {
                Sections sections = stepikApiClient.sections()
                        .get()
                        .id(sectionsIds)
                        .execute();

                for (Section section : sections.getSections()) {
                    SectionNode sectionNode = getChildById(section.getId());
                    if (sectionNode != null) {
                        sectionNode.setData(section);
                    } else {
                        SectionNode item = new SectionNode(this, section);
                        if (item.getLessonNodes().size() > 0) {
                            getSectionNodes().add(item);
                        }
                    }
                }

                clearMapNodes();
                sortChildren();
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
            List<Long> authorsIds = data.getAuthors();
            if (authorsIds.size() > 0) {
                try {
                    Users users = authAndGetStepikApiClient().users()
                            .get()
                            .id(authorsIds)
                            .execute();
                    authors = users.getUsers();
                } catch (StepikClientException e) {
                    return Collections.emptyList();
                }
            }
        }
        return authors != null ? authors : Collections.emptyList();
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

    @Override
    public long getCourseId() {
        return getId();
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
        sortChildren();
        clearMapNodes();
    }

    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (SectionNode sectionNode : getSectionNodes()) {
            if (sectionNode.getStatus() != StudyStatus.SOLVED)
                return StudyStatus.UNCHECKED;
        }

        return StudyStatus.SOLVED;
    }

    @Override
    public List<SectionNode> getChildren() {
        return getSectionNodes();
    }

    @NotNull
    @Override
    public Course getData() {
        if (data == null) {
            data = new Course();
        }
        return data;
    }

    @Override
    public void setData(@Nullable Course data) {
        this.data = data;
    }
}
