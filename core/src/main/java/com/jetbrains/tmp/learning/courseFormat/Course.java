package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.users.User;
import org.stepik.api.objects.users.Users;

import java.util.ArrayList;
import java.util.List;

public class Course implements StudyItem {
    private org.stepik.api.objects.courses.Course data;
    @Nullable
    private List<User> authors;
    @Nullable
    private List<Section> sections;

    public Course() {
    }

    public Course(org.stepik.api.objects.courses.Course data) {
        this.data = data;

        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        List<Integer> authorsIds = data.getAuthors();
        Users users = stepikApiClient.users()
                .get()
                .id(authorsIds)
                .execute();

        setAuthors(users.getUsers());

        List<Integer> sectionsIds = data.getSections();
        Sections sections = stepikApiClient.sections()
                .get()
                .id(sectionsIds)
                .execute();

        ArrayList<Section> sectionsList = new ArrayList<>();
        for (org.stepik.api.objects.sections.Section section : sections.getSections()) {
            Section item = new Section(section);
            if (item.getLessons().size() > 0) {
                sectionsList.add(item);
            }
        }

        setSections(sectionsList);

        initCourse(true);
    }

    /**
     * Initializes state of course
     */
    public void initCourse(boolean isRestarted) {
        for (Section section : getSections()) {
            section.initSection(this, isRestarted);
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
    public int getId() {
        return getData().getId();
    }

    public void addSection(@NotNull Section section) {
        getSections().add(section);
        getSections().sort(StudyItemComparator.getInstance());
    }

    @Nullable
    public Section getSectionById(int id) {
        for (Section section : getSections()) {
            if (section.getId() == id) {
                return section;
            }
        }
        return null;
    }

    @Nullable
    public Lesson getLessonById(int id) {
        for (Section section : getSections()) {
            for (Lesson lesson : section.getLessons()) {
                if (lesson.getId() == id) {
                    return lesson;
                }
            }
        }
        return null;
    }

    @Nullable
    public Step getStepById(int id) {
        for (Section section : getSections()) {
            for (Lesson lesson : section.getLessons()) {
                for (Step step : lesson.getSteps()) {
                    if (step.getId() == id) {
                        return step;
                    }
                }
            }
        }
        return null;
    }

    @NotNull
    public List<Section> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    @SuppressWarnings("unused")
    public void setSections(@Nullable List<Section> sections) {
        this.sections = sections;
    }

    @Nullable
    public Section getSectionByDirName(@NotNull String dirName) {
        int id = EduUtils.parseDirName(dirName, EduNames.SECTION);
        return getSectionById(id);
    }

    public Lesson getLessonByDirName(@NotNull String name) {
        int id = EduUtils.parseDirName(name, EduNames.LESSON);
        return getLessonById(id);
    }

    @Transient
    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (Section section : getSections()) {
            if (section.getStatus() != StudyStatus.SOLVED)
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
    public Section getPrevSection(@NotNull Section section) {
        int position = section.getPosition();
        List<Section> children = getSections();
        for (int i = children.size() - 1; i >= 0; i--) {
            Section item = children.get(i);
            if (item.getPosition() < position) {
                return item;
            }
        }
        return null;
    }

    @Transient
    @Nullable
    public Section getNextSection(@NotNull Section section) {
        int position = section.getPosition();
        for (Section item : getSections()) {
            if (item.getPosition() > position) {
                return item;
            }
        }
        return null;
    }

    public org.stepik.api.objects.courses.Course getData() {
        if (data == null) {
            data = new org.stepik.api.objects.courses.Course();
        }
        return data;
    }

    public void setData(org.stepik.api.objects.courses.Course data) {
        this.data = data;
    }
}
