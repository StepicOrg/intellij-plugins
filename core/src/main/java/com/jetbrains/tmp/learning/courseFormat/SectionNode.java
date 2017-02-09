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

public class SectionNode extends Node<LessonNode> {
    private static final Logger logger = Logger.getInstance(SectionNode.class);
    private Section data;
    private List<LessonNode> lessonNodes;
    private Map<Long, LessonNode> mapLessonNodes;

    public SectionNode() {
    }

    public SectionNode(@NotNull final CourseNode parent, @NotNull Section data) {
        this.data = data;
        init(parent, true, null);
    }

    void init(@NotNull final CourseNode parent, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

            if (indicator != null) {
                indicator.setText("Refresh a section: " + getName());
                indicator.setText2("Update lessons");
            }

            List<Long> unitsIds = getData().getUnits();

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

                Map<Long, LessonNode> nodeMap = getMapLessonNodes();

                for (Lesson lesson : lessons.getLessons()) {
                    LessonNode lessonNode = nodeMap.get(lesson.getId());
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

                clearNodeMap();
            }
        } catch (StepikClientException logged) {
            logger.warn("A section initialization don't is fully", logged);
        }

        setParent(parent);

        for (LessonNode lessonNode : getLessonNodes()) {
            lessonNode.init(this, isRestarted, indicator);
        }
    }

    private void clearNodeMap() {
        mapLessonNodes = null;
    }

    @Transient
    private Map<Long, LessonNode> getMapLessonNodes() {
        if (mapLessonNodes == null) {
            mapLessonNodes = new HashMap<>();
            getLessonNodes().forEach(lessonNode -> mapLessonNodes.put(lessonNode.getId(), lessonNode));
        }
        return mapLessonNodes;
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
        clearNodeMap();
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

    @Override
    protected List<LessonNode> getChildren() {
        return getLessonNodes();
    }

    @Transient
    @Override
    public long getId() {
        return getData().getId();
    }

    public void setId(long id) {
        getData().setId(id);
    }

    @Override
    public long getCourseId() {
        return getData().getCourse();
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

    @Nullable
    LessonNode getLessonById(long id) {
        return getMapLessonNodes().get(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectionNode that = (SectionNode) o;

        return data != null ? data.equals(that.data) : that.data == null;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }
}
