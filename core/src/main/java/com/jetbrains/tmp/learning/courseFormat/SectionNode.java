package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.lessons.Lesson;
import org.stepik.api.objects.lessons.Lessons;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.units.Unit;
import org.stepik.api.objects.units.Units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */

public class SectionNode implements StudyNode {
    private Section data;
    @Nullable
    private List<LessonNode> lessonNodes;
    @Nullable
    private CourseNode courseNode;

    public SectionNode() {
    }

    public SectionNode(Section data) {
        this.data = data;

        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        List<Long> unitsIds = data.getUnits();

        Units units = stepikApiClient.units()
                .get()
                .id(unitsIds)
                .execute();

        Map<Long, Unit> unitsMap = new HashMap<>();

        List<Long> lessonsIds = new ArrayList<>();

        units.getUnits().forEach(unit -> {
            long lessonId = unit.getLesson();
            lessonsIds.add(lessonId);
            unitsMap.put(lessonId, unit);
        });

        Lessons lessons = stepikApiClient.lessons()
                .get()
                .id(lessonsIds)
                .execute();

        ArrayList<LessonNode> lessonsList = new ArrayList<>();
        for (Lesson lesson : lessons.getLessons()) {
            LessonNode item = new LessonNode(lesson, unitsMap.get(lesson.getId()));
            if (item.getStepNodes().size() > 0) {
                lessonsList.add(item);
            }
        }

        setLessonNodes(lessonsList);
    }

    void initSection(@Nullable final CourseNode courseNode, boolean isRestarted) {
        setCourseNode(courseNode);
        for (LessonNode lessonNode : getLessonNodes()) {
            lessonNode.initLesson(this, isRestarted);
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
    public List<LessonNode> getLessonNodes() {
        if (lessonNodes == null) {
            lessonNodes = new ArrayList<>();
        }
        return lessonNodes;
    }

    @SuppressWarnings("unused")
    public void setLessonNodes(@Nullable List<LessonNode> lessonNodes) {
        this.lessonNodes = lessonNodes;
    }

    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (LessonNode lessonNode : getLessonNodes()) {
            if (lessonNode.getPosition() != -1 && lessonNode.getStatus() != StudyStatus.SOLVED)
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
        if (courseNode != null) {
            return courseNode.getPath() + "/" + getDirectory();
        } else {
            return getDirectory();
        }
    }

    @Transient
    @Nullable
    public CourseNode getCourseNode() {
        return courseNode;
    }

    @Transient
    public void setCourseNode(@Nullable CourseNode courseNode) {
        this.courseNode = courseNode;
    }

    @Transient
    @Override
    public long getId() {
        return getData().getId();
    }

    @Transient
    public void setId(long id) {
        getData().setId(id);
    }

    @Transient
    @Nullable
    public LessonNode getLastLesson() {
        List<LessonNode> children = getLessonNodes();
        int lessonsCount = children.size();

        if (lessonsCount == 0) {
            return null;
        }

        return children.get(lessonsCount - 1);
    }

    @Transient
    @Nullable
    public LessonNode getFirstLesson() {
        List<LessonNode> children = getLessonNodes();
        if (children.size() == 0) {
            return null;
        }

        return children.get(0);
    }

    @Transient
    @Nullable
    public LessonNode getPrevLesson(@NotNull LessonNode lessonNode) {
        int position = lessonNode.getPosition();
        List<LessonNode> children = getLessonNodes();
        for (int i = children.size() - 1; i >= 0; i--) {
            LessonNode item = children.get(i);
            if (item.getPosition() < position) {
                return item;
            }
        }
        return null;
    }

    @Transient
    @Nullable
    public LessonNode getNextLesson(@NotNull LessonNode lessonNode) {
        int position = lessonNode.getPosition();
        for (LessonNode item : getLessonNodes()) {
            if (item.getPosition() > position) {
                return item;
            }
        }
        return null;
    }

    public Section getData() {
        if (data == null) {
            data = new Section();
        }
        return data;
    }

    public void setData(Section data) {
        this.data = data;
    }

    public void addLesson(LessonNode lessonNode) {
        getLessonNodes().add(lessonNode);
    }
}
