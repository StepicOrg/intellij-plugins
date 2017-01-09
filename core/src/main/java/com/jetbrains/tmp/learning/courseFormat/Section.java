package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.lessons.Lessons;
import org.stepik.api.objects.units.Unit;
import org.stepik.api.objects.units.Units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */

public class Section implements StudyItem {
    private org.stepik.api.objects.sections.Section data;
    @Nullable
    private List<Lesson> lessons;
    @Nullable
    private Course course;

    public Section() {
    }

    public Section(org.stepik.api.objects.sections.Section data) {
        this.data = data;

        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        List<Integer> unitsIds = data.getUnits();

        Units units = stepikApiClient.units()
                .get()
                .id(unitsIds)
                .execute();

        Map<Integer, Unit> unitsMap = new HashMap<>();

        List<Integer> lessonsIds = new ArrayList<>();

        units.getUnits().forEach(unit -> {
            int lessonId = unit.getLesson();
            lessonsIds.add(lessonId);
            unitsMap.put(lessonId, unit);
        });

        Lessons lessons = stepikApiClient.lessons()
                .get()
                .id(lessonsIds)
                .execute();

        ArrayList<Lesson> lessonsList = new ArrayList<>();
        for (org.stepik.api.objects.lessons.Lesson lesson : lessons.getLessons()) {
            Lesson item = new Lesson(lesson, unitsMap.get(lesson.getId()));
            if (item.getSteps().size() > 0) {
                lessonsList.add(item);
            }
        }

        setLessons(lessonsList);
    }

    void initSection(@Nullable final Course course, boolean isRestarted) {
        setCourse(course);
        for (Lesson lesson : getLessons()) {
            lesson.initLesson(this, isRestarted);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return getData().getTitle();
    }

    @Transient
    @Override
    public int getPosition() {
        return getData().getPosition();
    }

    @Transient
    public void setPosition(int position) {
        getData().setPosition(position);
    }

    @NotNull
    public List<Lesson> getLessons() {
        if (lessons == null) {
            lessons = new ArrayList<>();
        }
        return lessons;
    }

    @SuppressWarnings("unused")
    public void setLessons(@Nullable List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (Lesson lesson : getLessons()) {
            if (lesson.getPosition() != -1 && lesson.getStatus() != StudyStatus.SOLVED)
                return StudyStatus.UNCHECKED;
        }

        return StudyStatus.SOLVED;
    }

    @NotNull
    @Override
    public String getDirectory() {
        return EduNames.SECTION + getId();
    }

    @NotNull
    @Override
    public String getPath() {
        if (course != null) {
            return course.getPath() + "/" + getDirectory();
        } else {
            return getDirectory();
        }
    }

    @Transient
    @Nullable
    public Course getCourse() {
        return course;
    }

    @Transient
    public void setCourse(@Nullable Course course) {
        this.course = course;
    }

    @Transient
    @Override
    public int getId() {
        return getData().getId();
    }

    @Transient
    public void setId(int id) {
        getData().setId(id);
    }

    @Transient
    @Nullable
    public Lesson getLastLesson() {
        List<Lesson> children = getLessons();
        int lessonsCount = children.size();

        if (lessonsCount == 0) {
            return null;
        }

        return children.get(lessonsCount - 1);
    }

    @Transient
    @Nullable
    public Lesson getFirstLesson() {
        List<Lesson> children = getLessons();
        if (children.size() == 0) {
            return null;
        }

        return children.get(0);
    }

    @Transient
    @Nullable
    public Lesson getPrevLesson(@NotNull Lesson lesson) {
        int position = lesson.getPosition();
        List<Lesson> children = getLessons();
        for (int i = children.size() - 1; i >= 0; i--) {
            Lesson item = children.get(i);
            if (item.getPosition() < position) {
                return item;
            }
        }
        return null;
    }

    @Transient
    @Nullable
    public Lesson getNextLesson(@NotNull Lesson lesson) {
        int position = lesson.getPosition();
        for (Lesson item : getLessons()) {
            if (item.getPosition() > position) {
                return item;
            }
        }
        return null;
    }

    public org.stepik.api.objects.sections.Section getData() {
        if (data == null) {
            data = new org.stepik.api.objects.sections.Section();
        }
        return data;
    }

    public void setData(org.stepik.api.objects.sections.Section data) {
        this.data = data;
    }

    public void addLesson(Lesson lesson) {
        getLessons().add(lesson);
    }
}
