package org.stepik.plugin.utils

import org.intellij.lang.annotations.Language
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.courses.Course
import org.stepik.api.objects.courses.Courses
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.lessons.Lesson
import org.stepik.api.objects.lessons.Lessons
import org.stepik.api.objects.sections.Section
import org.stepik.api.objects.sections.Sections
import org.stepik.api.objects.units.Unit
import org.stepik.api.objects.units.Units
import org.stepik.core.common.Loggable
import org.stepik.core.projectWizard.EMPTY_STUDY_OBJECT
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.templates.Templater
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils : Loggable {

    @Language("HTML")
    private val DEFAULT_DESCRIPTION = "<b>A course does not selected.</b><br>" +
            "<ul>" +
            "<li>Select a course from a list.</li>" +
            "<li>Push on a refresh button if a course list is a empty.</li>" +
            "<li>Write a link to a course (example, https://stepik.org/187/) or a id of course.</li>" +
            "</ul>"

    private val mainPattern = Pattern.compile(
            "(?:^|.*/)(course|lesson)(?=(?:(?:/[^/]*-)|/)(\\d+)(?:/|$))(.*)")
    private val unitPattern = Pattern.compile("(?:.*)[?|&]unit=(\\d+)(?:$|&)")
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

        val matcher = mainPattern.matcher(link)

        if (matcher.matches()) {
            val `object` = matcher.group(1)
            val id = java.lang.Long.parseLong(matcher.group(2))
            val params = matcher.group(3)

            if ("course" == `object`) {
                return getCourseStudyObject(id)
            } else if ("lesson" == `object`) {
                return getLessonStudyObject(id, params)
            }
        }

        return EMPTY_STUDY_OBJECT
    }

    private fun getLessonStudyObject(id: Long, params: String): StudyObject {
        val unitId = parseUnitId(params)

        return getLessonStudyObject(id, unitId)
    }

    fun parseUnitId(link: String): Long {
        val matcher: Matcher = unitPattern.matcher(link)
        var unitId: Long = 0
        if (matcher.matches()) {
            unitId = java.lang.Long.parseLong(matcher.group(1))
        }
        return unitId
    }

    private fun getLessonStudyObject(lessonId: Long, unitId: Long): StudyObject {
        val stepikApiClient = authAndGetStepikApiClient()
        val unitLesson = getCompoundUnitLessonStudyObject(stepikApiClient, unitId, lessonId)

        val unit = unitLesson.unit

        if (unit.id != 0L) {
            val section = getSectionStudyObject(stepikApiClient, unit.section.toLong())

            if (section != null) {
                return getCourseStudyObject(section.course.toLong())
            }
        }

        return unitLesson
    }

    private fun getCompoundUnitLessonStudyObject(
            stepikApiClient: StepikApiClient,
            unitId: Long,
            lessonId: Long): CompoundUnitLesson {
        var units: Units? = null

        if (unitId != 0L) {
            units = try {
                stepikApiClient.units()
                        .get()
                        .id(unitId)
                        .execute()
            } catch (e: StepikClientException) {
                logger.warn(e)
                Units()
            }
        }

        var unit: Unit? = null

        if (unitId != 0L && !units!!.isEmpty) {
            unit = units.first
        }

        val lesson = getLesson(lessonId, stepikApiClient)

        return if (lesson != null) CompoundUnitLesson(unit, lesson) else CompoundUnitLesson()
    }

    private fun getSectionStudyObject(
            stepikApiClient: StepikApiClient,
            sectionId: Long): Section? {
        var sections: Sections? = null

        if (sectionId != 0L) {
            try {
                sections = stepikApiClient.sections()
                        .get()
                        .id(sectionId)
                        .execute()
            } catch (e: StepikClientException) {
                logger.warn(e)
                return null
            }

        }

        return if (sectionId != 0L && !sections!!.isEmpty) {
            sections.first
        } else null
    }

    private fun getLesson(lessonId: Long, stepikApiClient: StepikApiClient): Lesson? {
        var lessons: Lessons? = null

        if (lessonId != 0L) {
            try {
                lessons = stepikApiClient.lessons()
                        .get()
                        .id(lessonId)
                        .execute()
            } catch (e: StepikClientException) {
                logger.warn(e)
                return null
            }

        }

        return if (lessonId != 0L && !lessons!!.isEmpty) {
            lessons.first
        } else null
    }

    private fun getCourseStudyObject(id: Long): StudyObject {
        val stepikApiClient = authAndGetStepikApiClient()
        val course = getCourse(stepikApiClient, id)
        return course ?: EMPTY_STUDY_OBJECT
    }

    private fun getCourse(
            stepikApiClient: StepikApiClient,
            id: Long): Course? {
        var courses: Courses? = null

        if (id != 0L) {
            try {
                courses = stepikApiClient.courses()
                        .get()
                        .id(id)
                        .execute()
            } catch (e: StepikClientException) {
                logger.warn(e)
                return null
            }

        }

        return if (id != 0L && !courses!!.isEmpty) {
            courses.first
        } else null
    }

    private fun isFillOfInt(link: String): Boolean {
        return link.matches("[0-9]+".toRegex())
    }

    fun getCourseDescription(studyObject: StudyObject): String {
        if (studyObject.id == 0L) {
            return DEFAULT_DESCRIPTION
        } else {
            val map = HashMap<String, Any?>()
            map["studyObject"] = studyObject

            val utcUpdateDate = studyObject.updateDate
            val localUpdateDate = timeOutFormat.format(utcUpdateDate)

            map["updateDate"] = localUpdateDate

            if (studyObject is Course) {
                map["summary"] = studyObject.summary
            }

            return Templater.processTemplate("catalog/description", map)
        }
    }
}
