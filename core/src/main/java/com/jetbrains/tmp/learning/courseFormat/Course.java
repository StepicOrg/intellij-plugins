package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.stepik.StepikUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Course implements StudyItem {
    @Nullable
    @Expose
    private List<StepikUser> authors;
    @Nullable
    @Expose
    private String description;
    @Nullable
    @Expose
    private String name;
    @Expose
    private int id;
    @Expose
    private boolean isAdaptive = false;
    @Nullable
    @Expose
    private List<Section> sections;

    public Course() {
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
    public List<StepikUser> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public void setAuthors(@Nullable List<StepikUser> authors) {
        this.authors = authors;
    }

    @NotNull
    @Override
    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Transient
    @Override
    public int getPosition() {
        throw new UnsupportedOperationException("Course not support getPosition()");
    }

    @Transient
    @Override
    public void setPosition(int position) {
        throw new UnsupportedOperationException("Course not support setPosition()");
    }

    @NotNull
    public String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public boolean isAdaptive() {
        return isAdaptive;
    }

    public void setAdaptive(boolean adaptive) {
        isAdaptive = adaptive;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Transient
    @Nullable
    @Override
    public Course getCourse() {
        return this;
    }

    public void addSection(@NotNull Section section) {
        getSections().add(section);
        getSections().sort(StudyItemComparator.getInstance());
    }

    @Nullable
    public Section getSectionById(int id) {
        for (Section section : getSections()) {
            if (section.getId() == id)
                return section;
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
                for (Step step : lesson.getStepList()) {
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

    @Override
    public void updatePath() {
        getSections().forEach(StudyItem::updatePath);
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

    @Transient
    @NotNull
    public String getCacheDirectory() {
        return FileUtil.join(PathManager.getConfigPath(), "courses", Integer.toString(id));
    }
}
