package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Section implements StudyItem {
    @Expose
    private String name;
    @Expose
    private List<Lesson> lessons = new ArrayList<>();
    @Expose
    private int index;
    @Expose
    private int id;
    @Transient
    private Course myCourse = null;
    @Transient
    private String directory;
    @Transient
    private String path;

    public Section() {
    }

    public void initSection(final Course course, boolean isRestarted) {
        setCourse(course);
        for (Lesson lesson : getLessons()) {
            lesson.initLesson(this, isRestarted);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
        directory = EduNames.SECTION + index;
        updatePath();
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void addLessons(@NotNull List<Lesson> lessons) {
        this.lessons.addAll(lessons);
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    @Override
    public StudyStatus getStatus() {
        for (Lesson lesson : lessons) {
            if (lesson.getIndex() != -1 && lesson.getStatus() != StudyStatus.Solved)
                return StudyStatus.Unchecked;
        }

        return StudyStatus.Solved;
    }

    @Override
    public void updatePath() {
        if (path == null) {
            return;
        }

        path = null;

        lessons.forEach(StudyItem::updatePath);
    }

    @NotNull
    @Override
    public String getDirectory() {
        return directory;
    }

    @NotNull
    @Override
    public String getPath() {
        if (path == null) {
            path = myCourse.getPath() + "/" + getDirectory();
        }
        return path;
    }

    @Transient
    public void setCourse(Course course) {
        this.myCourse = course;
    }

    @Transient
    public Course getCourse() {
        return myCourse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        return id == section.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
