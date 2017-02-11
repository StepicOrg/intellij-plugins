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

import java.util.Collections;
import java.util.List;

import static com.jetbrains.tmp.learning.stepik.StepikConnectorLogin.authAndGetStepikApiClient;

public class CourseNode extends Node<Course, SectionNode, Section, LessonNode> {
    private static final Logger logger = Logger.getInstance(CourseNode.class);
    private List<User> authors;

    public CourseNode() {
    }

    public CourseNode(@NotNull Course data, @Nullable ProgressIndicator indicator) {
        super(data, indicator);
    }

    @Override
    protected List<Section> getChildDataList() {
        Sections sections = new Sections();
        try {
            StepikApiClient stepikApiClient = authAndGetStepikApiClient();

            List<Long> sectionsIds = getData().getSections();
            if (sectionsIds.size() > 0) {
                sections = stepikApiClient.sections()
                        .get()
                        .id(sectionsIds)
                        .execute();


            }
        } catch (StepikClientException | IllegalAccessException | InstantiationException logged) {
            logger.warn("A course initialization don't is fully", logged);
        }

        return sections.getSections();
    }

    public void init(@Nullable StudyNode parent, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        if (indicator != null) {
            indicator.setText("Refresh " + getName());
            indicator.setText2("Update sections");
        }

        authors = null;

        super.init(parent, isRestarted, indicator);
    }

    @Override
    protected Class<SectionNode> getChildClass() {
        return SectionNode.class;
    }

    @Override
    protected Class<Course> getDataClass() {
        return Course.class;
    }

    @SuppressWarnings("unused")
    @NotNull
    public List<User> getAuthors() {
        if (authors == null) {
            List<Long> authorsIds;
            try {
                authorsIds = getData().getAuthors();
            } catch (IllegalAccessException | InstantiationException e) {
                return Collections.emptyList();
            }
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
        try {
            return getData().getTitle();
        } catch (IllegalAccessException | InstantiationException e) {
            return "";
        }
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public long getCourseId() {
        return getId();
    }
}
