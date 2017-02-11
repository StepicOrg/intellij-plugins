package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
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

public class SectionNode extends Node<LessonNode, Section> {
    private static final Logger logger = Logger.getInstance(SectionNode.class);
    private Section data;
    private List<LessonNode> lessonNodes;

    public SectionNode() {
    }

    public SectionNode(@NotNull final StudyNode parent, @NotNull Section data) {
        super(parent, data);
    }

    protected void init(@Nullable final StudyNode parent, boolean isRestarted, @Nullable ProgressIndicator indicator) {
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

                for (Lesson lesson : lessons.getLessons()) {
                    LessonNode lessonNode = getChildById(lesson.getId());
                    if (lessonNode != null) {
                        lessonNode.getData().setLesson(lesson);
                        lessonNode.getData().setUnit(unitsMap.get(lesson.getId()));
                    } else {
                        LessonNode item = new LessonNode(this, new CompoundUnitLesson(unitsMap.get(lesson.getId()),
                                lesson));
                        if (item.getStepNodes().size() > 0) {
                            getLessonNodes().add(item);
                        }
                    }
                }

                clearMapNodes();
                sortChildren();
            }
        } catch (StepikClientException logged) {
            logger.warn("A section initialization don't is fully", logged);
        }

        setParent(parent);

        for (LessonNode lessonNode : getLessonNodes()) {
            lessonNode.init(this, isRestarted, indicator);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return getData().getTitle();
    }

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
        sortChildren();
        clearMapNodes();
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

    @Override
    public List<LessonNode> getChildren() {
        return getLessonNodes();
    }

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

    @NotNull
    public Section getData() {
        if (data == null) {
            data = new Section();
        }
        return data;
    }

    public void setData(@Nullable Section data) {
        this.data = data;
    }

    @NotNull
    @Override
    String getDirectoryPrefix() {
        return EduNames.SECTION;
    }
}
