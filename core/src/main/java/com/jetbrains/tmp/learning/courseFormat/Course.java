package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.intellij.lang.Language;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.stepik.StepikUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Course implements StudyItem {
    @Expose
    private List<StepikUser> authors = new ArrayList<>();
    @Expose
    private String description;
    @Expose
    private String name;
    private String myCourseDirectory = "";
    @Expose
    private int id;
    private boolean myUpToDate;
    @Expose
    private boolean isAdaptive = false;
    @Expose
    @SerializedName("language")
    private String myLanguage = "Python";
    @Expose
    private List<Section> sections = new ArrayList<>();

    //this field is used to distinguish ordinary and CheckIO projects,
    //"PyCharm" is used here for historical reasons
    private String courseType = EduNames.PYCHARM;
    private String courseMode = EduNames.STUDY; //this field is used to distinguish study and course creator modes

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

    @NotNull
    public List<StepikUser> getAuthors() {
        return authors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    @Override
    public int getIndex() {
        throw new UnsupportedOperationException("Course not support getIndex()");
    }

    @Transient
    @Override
    public void setIndex(int index) {
        throw new UnsupportedOperationException("Course not support setIndex()");
    }

    public String getCourseDirectory() {
        return myCourseDirectory;
    }

    public void setCourseDirectory(@NotNull final String courseDirectory) {
        myCourseDirectory = courseDirectory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUpToDate() {
        return myUpToDate;
    }

    public void setUpToDate(boolean upToDate) {
        myUpToDate = upToDate;
    }

    public Language getLanguageById() {
        return Language.findLanguageByID(myLanguage);
    }

    public String getLanguage() {
        return myLanguage;
    }

    public void setLanguage(@NotNull final String language) {
        myLanguage = language;
    }

    public void setAuthors(@NotNull List<StepikUser> authors) {
        this.authors = authors;
    }

    @NotNull
    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(@NotNull String courseType) {
        this.courseType = courseType;
    }

    public boolean isAdaptive() {
        return isAdaptive;
    }

    public void setAdaptive(boolean adaptive) {
        isAdaptive = adaptive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseMode() {
        return courseMode;
    }

    public void setCourseMode(String courseMode) {
        this.courseMode = courseMode;
    }

    public void addSection(@NotNull Section section) {
        sections.add(section);
    }

    public void setSections(@Nullable List<Section> sections) {
        if (sections == null)
            sections = new ArrayList<>();

        this.sections = sections;
    }

    @Nullable
    private Section getSectionOfIndex(int index) {
        for (Section section : sections) {
            if (section.getIndex() == index)
                return section;
        }
        return null;
    }

    @NotNull
    public List<Section> getSections() {
        return sections;
    }

    public void addSectionWithSetIndex(Section section) {
        section.setIndex(sections.size() + 1);
        sections.add(section);
    }

    public Lesson getLessonOfIndex(int index) {
        for (Section section : getSections()) {
            for (Lesson lesson : section.getLessons()) {
                if (lesson.getIndex() == index) {
                    return lesson;
                }
            }
        }
        return null;
    }

    public Lesson getLessonByDirName(@NotNull String name) {
        int index = EduUtils.getIndex(name, EduNames.LESSON);
        return getLessonOfIndex(index);
    }

    @Transient
    @Override
    public StudyStatus getStatus() {
        for (Section section : sections) {
            if (section.getStatus() != StudyStatus.Solved)
                return StudyStatus.Unchecked;
        }

        return StudyStatus.Solved;
    }

    @NotNull
    @Override
    public String getDirectory() {
        return "";
    }

    @NotNull
    @Override
    public String getPath() {
        return "";
    }

    @Override
    public void updatePath() {
        sections.forEach(StudyItem::updatePath);
    }

    public Section getSectionByDirName(@NotNull String valueName) {
        int index = EduUtils.getIndex(valueName, EduNames.SECTION);
        return getSectionOfIndex(index);
    }
}
