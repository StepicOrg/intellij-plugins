package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
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
    private static final Logger logger = Logger.getInstance(SectionNode.class);
    private Section data;
    private List<LessonNode> lessonNodes;
    private CourseNode courseNode;

    public SectionNode() {
    }

    public SectionNode(@NotNull final CourseNode courseNode, @NotNull Section data) {
        this.data = data;
        init(courseNode, true, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectionNode that = (SectionNode) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        //noinspection SimplifiableIfStatement
        if (lessonNodes != null ? !lessonNodes.equals(that.lessonNodes) : that.lessonNodes != null) return false;
        return courseNode != null ? courseNode.equals(that.courseNode) : that.courseNode == null;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (lessonNodes != null ? lessonNodes.hashCode() : 0);
        result = 31 * result + (courseNode != null ? courseNode.hashCode() : 0);
        return result;
    }

    void init(@NotNull final CourseNode courseNode, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

            if (indicator != null) {
                indicator.setText("Refresh a section: " + getName());
                indicator.setText2("Update lessons");
            }

            List<Long> unitsIds = data.getUnits();

            if (unitsIds.size() > 0) {
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

                for (Lesson lesson : lessons.getLessons()) {
                    LessonNode lessonNode = getLessonById(lesson.getId());
                    if (lessonNode != null) {
                        lessonNode.setData(lesson);
                        lessonNode.setUnit(unitsMap.get(lesson.getId()));
                    } else {
                        LessonNode item = new LessonNode(this, lesson, unitsMap.get(lesson.getId()));
                        if (item.getStepNodes().size() > 0) {
                            getLessonNodes().add(item);
                        }
                    }
                }
            }
        } catch (StepikClientException logged) {
            logger.warn("A section initialization don't is fully", logged);
        }

        setCourseNode(courseNode);

        for (LessonNode lessonNode : getLessonNodes()) {
            lessonNode.init(this, isRestarted, indicator);
        }
    }

    @Nullable
    private LessonNode getLessonById(long id) {
        for (LessonNode lessonNode : getLessonNodes()) {
            if (lessonNode.getId() == id) {
                return lessonNode;
            }
        }
        return null;
    }

    @Transient
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

    @Transient
    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (LessonNode lessonNode : getLessonNodes()) {
            if (lessonNode.getPosition() != -1 && lessonNode.getStatus() != StudyStatus.SOLVED)
                return StudyStatus.UNCHECKED;
        }

        return StudyStatus.SOLVED;
    }

    @Transient
    @NotNull
    @Override
    public String getDirectory() {
        return EduNames.SECTION + getId();
    }

    @Transient
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

    public void setCourseNode(@Nullable CourseNode courseNode) {
        this.courseNode = courseNode;
    }

    @Transient
    @Override
    public long getId() {
        return getData().getId();
    }

    public void setId(long id) {
        getData().setId(id);
    }

    @Nullable
    @Override
    public CourseNode getCourse() {
        return courseNode;
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

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public Section getData() {
        if (data == null) {
            data = new Section();
        }
        return data;
    }

    @SuppressWarnings("unused")
    public void setData(@Nullable Section data) {
        this.data = data;
    }

    public void addLesson(@Nullable LessonNode lessonNode) {
        getLessonNodes().add(lessonNode);
    }
}
