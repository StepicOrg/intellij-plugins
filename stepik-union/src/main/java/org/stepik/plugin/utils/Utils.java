package org.stepik.plugin.utils;

import com.intellij.openapi.diagnostic.Logger;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.courses.Courses;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.lessons.Lesson;
import org.stepik.api.objects.lessons.Lessons;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.units.Unit;
import org.stepik.api.objects.units.Units;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.plugin.projectWizard.StepikProjectGenerator.EMPTY_STUDY_OBJECT;

/**
 * @author meanmail
 */
public class Utils {
    private static final Logger logger = Logger.getInstance(Utils.class);
    @Language("HTML")
    private static final String DEFAULT_DESCRIPTION =
            "<b>A course does not selected.</b><br>" +
                    "<ul>" +
                    "<li>Select a course from a list.</li>" +
                    "<li>Push on a refresh button if a course list is a empty.</li>" +
                    "<li>Write a link to a course (example, https://stepik.org/187/) or a id of course.</li>" +
                    "</ul>";

    private static final Pattern mainPattern = Pattern.compile(
            "(?:^|.*/)(course|lesson)(?=(?:(?:/[^/]*-)|/)(\\d+)(?:/|$))(.*)");
    private static final Pattern unitPattern = Pattern.compile("(?:.*)[?|&]unit=(\\d+)(?:$|&)");

    public static StudyObject getStudyObjectFromLink(@NotNull String link) {
        // https://stepik.org/course/Основы-программирования-для-Linux-548
        // https://stepik.org/course/548
        // https://stepik.org/lesson/Основной-инструментарий-разработчика-Linux-26302/step/1?course=Основы-программирования-для-Linux&unit=8180
        // https://stepik.org/course/Основы-программирования-для-Linux-548/syllabus?module=1
        // 548

        if (isFillOfInt(link)) {
            return getCourseStudyObject(Long.parseLong(link));
        }

        Matcher matcher = mainPattern.matcher(link);

        if (matcher.matches()) {
            String object = matcher.group(1);
            long id = Long.parseLong(matcher.group(2));
            String params = matcher.group(3);

            if ("course".equals(object)) {
                return getCourseStudyObject(id);
            } else if ("lesson".equals(object)) {
                return getLessonStudyObject(id, params);
            }
        }

        return EMPTY_STUDY_OBJECT;
    }

    private static StudyObject getLessonStudyObject(long id, String params) {
        long unitId = parseUnitId(params);

        return getLessonStudyObject(id, unitId);
    }

    static long parseUnitId(String link) {
        Matcher matcher;
        matcher = unitPattern.matcher(link);
        long unitId = 0;
        if (matcher.matches()) {
            unitId = Long.parseLong(matcher.group(1));
        }
        return unitId;
    }

    private static StudyObject getLessonStudyObject(long lessonId, long unitId) {
        StepikApiClient stepikApiClient = authAndGetStepikApiClient();
        CompoundUnitLesson unitLesson = getCompoundUnitLessonStudyObject(stepikApiClient, unitId, lessonId);

        Unit unit = unitLesson.getUnit();

        if (unit.getId() != 0) {
            Section section = getSectionStudyObject(stepikApiClient, unit.getSection());

            if (section != null) {
                return getCourseStudyObject(section.getCourse());
            }
        }

        return unitLesson;
    }

    @NotNull
    private static CompoundUnitLesson getCompoundUnitLessonStudyObject(
            @NotNull StepikApiClient stepikApiClient,
            long unitId,
            long lessonId) {
        Units units = null;

        if (unitId != 0) {
            try {
                units = stepikApiClient.units()
                        .get()
                        .id(unitId)
                        .execute();
            } catch (StepikClientException e) {
                logger.warn(e);
                units = new Units();
            }
        }

        Unit unit = null;

        if (unitId != 0 && !units.isEmpty()) {
            unit = units.getFirst();
        }

        Lesson lesson = getLesson(lessonId, stepikApiClient);

        return lesson != null ? new CompoundUnitLesson(unit, lesson) : new CompoundUnitLesson();
    }

    private static Section getSectionStudyObject(
            @NotNull StepikApiClient stepikApiClient,
            long sectionId) {
        Sections sections = null;

        if (sectionId != 0) {
            try {
                sections = stepikApiClient.sections()
                        .get()
                        .id(sectionId)
                        .execute();
            } catch (StepikClientException e) {
                logger.warn(e);
                return null;
            }
        }

        if (sectionId != 0 && !sections.isEmpty()) {
            return sections.getFirst();
        }
        return null;
    }

    @Nullable
    private static Lesson getLesson(long lessonId, @NotNull StepikApiClient stepikApiClient) {
        Lessons lessons = null;

        if (lessonId != 0) {
            try {
                lessons = stepikApiClient.lessons()
                        .get()
                        .id(lessonId)
                        .execute();
            } catch (StepikClientException e) {
                logger.warn(e);
                return null;
            }
        }

        if (lessonId != 0 && !lessons.isEmpty()) {
            return lessons.getFirst();
        }
        return null;
    }

    @NotNull
    private static StudyObject getCourseStudyObject(long id) {
        StepikApiClient stepikApiClient = authAndGetStepikApiClient();
        Course course = getCourse(stepikApiClient, id);
        return course != null ? course : EMPTY_STUDY_OBJECT;
    }

    @Nullable
    private static Course getCourse(
            @NotNull StepikApiClient stepikApiClient,
            long id) {
        Courses courses = null;

        if (id != 0) {
            try {
                courses = stepikApiClient.courses()
                        .get()
                        .id(id)
                        .execute();
            } catch (StepikClientException e) {
                logger.warn(e);
                return null;
            }
        }

        if (id != 0 && !courses.isEmpty()) {
            return courses.getFirst();
        }
        return null;
    }

    private static boolean isFillOfInt(@NotNull String link) {
        return link.matches("[0-9]+");
    }

    @NotNull
    public static String getCourseDescription(@NotNull StudyObject studyObject) {
        if (studyObject.getId() == 0) {
            return DEFAULT_DESCRIPTION;
        } else {
            return studyObject.getDescription();
        }
    }
}
