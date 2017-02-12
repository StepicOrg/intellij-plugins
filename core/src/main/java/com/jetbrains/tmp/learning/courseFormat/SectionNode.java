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
import org.stepik.api.objects.lessons.Lessons;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.units.Unit;
import org.stepik.api.objects.units.Units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */

public class SectionNode extends Node<Section, LessonNode, CompoundUnitLesson, StepNode> {
    private static final Logger logger = Logger.getInstance(SectionNode.class);

    public SectionNode() {
    }

    public SectionNode(@NotNull Section data, @Nullable ProgressIndicator indicator) {
        super(data, indicator);
    }

    public void init(@Nullable final StudyNode parent, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        if (indicator != null) {
            indicator.setText("Refresh a section: " + getName());
            indicator.setText2("Update lessons");
        }

        super.init(parent, isRestarted, indicator);
    }

    @Override
    protected void loadData(long id) {
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
            Sections sections = stepikApiClient.sections()
                    .get()
                    .id(id)
                    .execute();
            Section data;
            if (!sections.isEmpty()) {
                data = sections.getSections().get(0);
            } else {
                data = new Section();
                data.setId(id);
            }
            setData(data);
        } catch (StepikClientException logged) {
            logger.warn(String.format("Failed load section data id=%d", id), logged);
        }
    }

    @Override
    protected Class<LessonNode> getChildClass() {
        return LessonNode.class;
    }

    @NotNull
    @Override
    public String getName() {
        try {
            return getData().getTitle();
        } catch (IllegalAccessException | InstantiationException e) {
            return "";
        }
    }

    @Override
    public int getPosition() {
        try {
            return getData().getPosition();
        } catch (IllegalAccessException | InstantiationException e) {
            return 0;
        }
    }

    @Override
    public long getCourseId() {
        try {
            return getData().getCourse();
        } catch (IllegalAccessException | InstantiationException e) {
            return 0;
        }
    }

    @Override
    protected Class<Section> getDataClass() {
        return Section.class;
    }

    @NotNull
    @Override
    String getDirectoryPrefix() {
        return EduNames.SECTION;
    }

    @Override
    protected List<CompoundUnitLesson> getChildDataList() {
        List<CompoundUnitLesson> objects = new ArrayList<>();
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

            List<Long> unitsIds;
            try {
                unitsIds = getData().getUnits();
            } catch (IllegalAccessException | InstantiationException e) {
                return objects;
            }

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

                lessons.getLessons()
                        .forEach(lesson -> objects.add(new CompoundUnitLesson(unitsMap.get(lesson.getId()), lesson)));
            }
        } catch (StepikClientException logged) {
            logger.warn("A section initialization don't is fully", logged);
        }

        return objects;
    }
}
