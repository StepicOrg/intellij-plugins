package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.lessons.Lessons;
import org.stepik.api.objects.units.Units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
@Deprecated
public class Section implements StudyItem {
    @Expose
    @Nullable
    private String name;
    @Expose
    @Nullable
    private List<Lesson> lessons;
    @Expose
    private int position;
    @Expose
    private int id;
    @Transient
    @Nullable
    private Course course;
    @Transient
    @Nullable
    private String directory;
    @Transient
    @Nullable
    private String path;

    public Section() {
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
        if (name == null) {
            name = "";
        }
        return name;
    }

    @Override
    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    void addLesson(@NotNull Lesson lesson) {
        getLessons().add(lesson);
    }

    public void addLessons(@NotNull List<Lesson> lessons) {
        getLessons().addAll(lessons);
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

    @Override
    public void updatePath() {
        path = null;

        getLessons().forEach(StudyItem::updatePath);
    }

    @NotNull
    @Override
    public String getDirectory() {
        if (directory == null) {
            directory = EduNames.SECTION + id;
            updatePath();
        }
        return directory;
    }

    @NotNull
    @Override
    public String getPath() {
        if (path == null) {
            if (course != null) {
                path = course.getPath() + "/" + getDirectory();
            } else {
                path = getDirectory();
            }
        }
        return path;
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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
        directory = null;
        updatePath();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (position != section.position) return false;
        if (id != section.id) return false;
        if (name != null ? !name.equals(section.name) : section.name != null) return false;
        //noinspection SimplifiableIfStatement
        if (lessons != null ? !lessons.equals(section.lessons) : section.lessons != null) return false;
        return course != null ? course.equals(section.course) : section.course == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (lessons != null ? lessons.hashCode() : 0);
        result = 31 * result + position;
        result = 31 * result + id;
        result = 31 * result + (course != null ? course.hashCode() : 0);
        return result;
    }

    public static Section fromSection(org.stepik.api.objects.sections.Section section) {
        Section result = new Section();

        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        result.setId(section.getId());

        int[] unitsIds = section.getUnits();

        Units units = stepikApiClient.units()
                .get()
                .id(unitsIds)
                .execute();

        List<Integer> lessonsIds = new ArrayList<>();
        Map<Integer, Integer> positions = new HashMap<>();

        units.getUnits().forEach(unit -> {
            int lessonId = unit.getLesson();
            lessonsIds.add(lessonId);
            positions.put(lessonId, unit.getPosition());
        });

        Lessons lessons = stepikApiClient.lessons()
                .get()
                .id(lessonsIds.toArray(new Integer[lessonsIds.size()]))
                .execute();

        ArrayList<Lesson> lessonsList = new ArrayList<>();
        for (org.stepik.api.objects.lessons.Lesson lesson : lessons.getLessons()) {
            lessonsList.add(Lesson.fromLesson(lesson, positions.getOrDefault(lesson.getId(), 0)));
        }

        result.setLessons(lessonsList);
        result.setName(section.getTitle());
        result.setPosition(section.getPosition());

        return result;
    }
}
