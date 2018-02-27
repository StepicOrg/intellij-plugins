package org.stepik.core.utils

import org.intellij.lang.annotations.Language
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.courses.Course
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.lessons.Lesson
import org.stepik.api.objects.sections.Section
import org.stepik.api.objects.units.Unit
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.common.Loggable
import org.stepik.core.templates.Templater
import java.text.SimpleDateFormat


object StepikUrlUtils : Loggable {

    @Language("HTML")
    private val DEFAULT_DESCRIPTION = "<b>A course does not selected.</b><br>" +
            "<ul>" +
            "<li>Select a course from a list.</li>" +
            "<li>Push on a refresh button if a course list is a empty.</li>" +
            "<li>Write a link to a course (example, https://stepik.org/187/) or a id of course.</li>" +
            "</ul>"

    private val mainPattern = "(?:^|.*/)(course|lesson)(?=(?:(?:/[^/]*-)|/)(\\d+)(?:/|$))(.*)".toRegex()
    private val unitPattern = "(?:.*)[?|&]unit=(\\d+)(?:$|&)".toRegex()
    private val timeOutFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss")

    fun getStudyObjectFromLink(link: String): StudyObject {
        // https://stepik.org/course/Основы-программирования-для-Linux-548
        // https://stepik.org/course/548
        // https://stepik.org/lesson/Основной-инструментарий-разработчика-Linux-26302/step/1?course=Основы-программирования-для-Linux&unit=8180
        // https://stepik.org/course/Основы-программирования-для-Linux-548/syllabus?module=1
        // 548

        if (isFillOfInt(link)) {
            return getCourseStudyObject(java.lang.Long.parseLong(link))
        }

        val matcher = mainPattern.matchEntire(link) ?: return EMPTY_STUDY_OBJECT

        val groups = matcher.groupValues
        val studyObject = groups[1]
        val id = groups[2].toLong()
        val params = groups[3]

        return when (studyObject) {
            "course" -> getCourseStudyObject(id)
            "lesson" -> getLessonStudyObject(id, params)
            else -> EMPTY_STUDY_OBJECT
        }
    }

    private fun getLessonStudyObject(id: Long, params: String): StudyObject {
        val unitId = parseUnitId(params)
        return getLessonStudyObject(id, unitId)
    }

    fun parseUnitId(link: String): Long {
        val matcher = unitPattern.matchEntire(link) ?: return 0
        return matcher.groupValues[1].toLong()
    }

    private fun getLessonStudyObject(lessonId: Long, unitId: Long): StudyObject {
        val stepikApiClient = authAndGetStepikApiClient()
        val unitLesson = getCompoundUnitLessonStudyObject(stepikApiClient, unitId, lessonId)

        val unit = unitLesson.unit

        if (unit.id == 0L) {
            return unitLesson
        }

        val section = getSectionStudyObject(stepikApiClient, unit.section.toLong()) ?: return unitLesson

        return getCourseStudyObject(section.course.toLong())
    }

    private fun getCompoundUnitLessonStudyObject(
            stepikApiClient: StepikApiClient,
            unitId: Long,
            lessonId: Long): CompoundUnitLesson {
        var unit = Unit()

        if (unitId != 0L) {
            try {
                unit = stepikApiClient.units()
                        .get()
                        .id(unitId)
                        .execute()
                        .firstOrDefault(Unit())
            } catch (e: StepikClientException) {
                logger.warn(e)
            }
        }

        val lesson = getLesson(lessonId, stepikApiClient) ?: return CompoundUnitLesson()

        return CompoundUnitLesson(unit, lesson)
    }

    private fun getSectionStudyObject(
            stepikApiClient: StepikApiClient,
            sectionId: Long): Section? {
        if (sectionId != 0L) {
            try {
                return stepikApiClient.sections()
                        .get()
                        .id(sectionId)
                        .execute()
                        .firstOrNull()
            } catch (e: StepikClientException) {
                logger.warn(e)
            }
        }

        return null
    }

    private fun getLesson(lessonId: Long, stepikApiClient: StepikApiClient): Lesson? {
        if (lessonId != 0L) {
            try {
                return stepikApiClient.lessons()
                        .get()
                        .id(lessonId)
                        .execute()
                        .firstOrNull()
            } catch (e: StepikClientException) {
                logger.warn(e)
            }
        }

        return null
    }

    private fun getCourseStudyObject(id: Long): StudyObject {
        return getCourse(authAndGetStepikApiClient(), id) ?: EMPTY_STUDY_OBJECT
    }

    private fun getCourse(
            stepikApiClient: StepikApiClient,
            id: Long): Course? {
        if (id != 0L) {
            try {
                return stepikApiClient.courses()
                        .get()
                        .id(id)
                        .execute()
                        .firstOrNull()
            } catch (e: StepikClientException) {
                logger.warn(e)
            }
        }

        return null
    }

    private fun isFillOfInt(link: String): Boolean {
        return link.matches("[0-9]+".toRegex())
    }

    fun getCourseDescription(studyObject: StudyObject): String {
        if (studyObject.id == 0L) {
            return DEFAULT_DESCRIPTION
        } else {
            val context = mutableMapOf<String, Any?>()
            context["studyObject"] = studyObject

            val utcUpdateDate = studyObject.updateDate
            val localUpdateDate = timeOutFormat.format(utcUpdateDate)

            context["updateDate"] = localUpdateDate

            if (studyObject is Course) {
                context["summary"] = studyObject.summary
            }

            return Templater.processTemplate("catalog/description", context)
        }
    }
}
