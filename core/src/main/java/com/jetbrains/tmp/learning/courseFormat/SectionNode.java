package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.lessons.Lessons;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.units.Unit;
import org.stepik.api.objects.units.Units;

import java.util.ArrayList;
import java.util.Collections;
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

    public SectionNode(@NotNull Project project, @NotNull Section data) {
        super(project, data);
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

    @Override
    public long getCourseId() {
        Section data = getData();
        return data != null ? getData().getCourse() : 0;
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
            Section data = getData();
            unitsIds = data != null ? data.getUnits() : Collections.emptyList();

            if (!unitsIds.isEmpty()) {
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
