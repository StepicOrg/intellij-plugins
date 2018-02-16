package org.stepik.core.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.courses.Courses;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.users.User;
import org.stepik.api.objects.users.Users;
import org.stepik.core.stepik.StepikAuthManager;

import java.util.Collections;
import java.util.List;

public class CourseNode extends Node<Course, SectionNode, Section, LessonNode> {
    private static final Logger logger = Logger.getInstance(CourseNode.class);
    private List<User> authors;

    public CourseNode() {
    }

    public CourseNode(@NotNull Project project, @NotNull StepikApiClient stepikApiClient, @NotNull Course data) {
        super(project, stepikApiClient, data);
    }

    @Override
    protected List<Section> getChildDataList(@NotNull StepikApiClient stepikApiClient) {
        Sections sections = new Sections();
        try {
            Course data = getData();
            List<Long> sectionsIds = data != null ? getData().getSections() : Collections.emptyList();
            if (!sectionsIds.isEmpty()) {
                sections = stepikApiClient.sections()
                        .get()
                        .id(sectionsIds)
                        .execute();


            }
        } catch (StepikClientException logged) {
            logger.warn("A course initialization don't is fully", logged);
        }

        return sections.getSections();
    }

    @Override
    public void init(@NotNull Project project, @NotNull StepikApiClient stepikApiClient, @Nullable StudyNode parent) {
        authors = null;
        super.init(project, stepikApiClient, parent);
    }

    @Override
    protected boolean loadData(@NotNull StepikApiClient stepikApiClient, long id) {
        try {
            Courses courses = stepikApiClient.courses()
                    .get()
                    .id(id)
                    .execute();

            Course data;

            if (!courses.isEmpty()) {
                data = courses.getFirst();
            } else {
                data = new Course();
                data.setId(id);
            }

            Course oldData = this.getData();
            setData(data);
            return oldData == null || !oldData.getUpdateDate().equals(data.getUpdateDate());
        } catch (StepikClientException logged) {
            logger.warn(String.format("Failed load course data id=%d", id), logged);
        }
        return true;
    }

    @Override
    protected Class<SectionNode> getChildClass() {
        return SectionNode.class;
    }

    @Override
    protected Class<Section> getChildDataClass() {
        return Section.class;
    }

    @Override
    protected Class<Course> getDataClass() {
        return Course.class;
    }

    @SuppressWarnings("unused")
    @NotNull
    public List<User> getAuthors(@NotNull StepikApiClient stepikApiClient) {
        if (authors == null) {
            List<Long> authorsIds;
            Course data = getData();
            authorsIds = data != null ? data.getAuthors() : Collections.emptyList();
            if (!authorsIds.isEmpty()) {
                try {
                    if (!StepikAuthManager.INSTANCE.isAuthenticated()) {
                        return Collections.emptyList();
                    }
                    Users users = stepikApiClient.users()
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

    @Override
    public long getCourseId(@NotNull StepikApiClient stepikApiClient) {
        return getId();
    }
}
