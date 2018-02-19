package org.stepik.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import org.stepik.api.objects.courses.Course


class UtilsTest {

    @Test
    fun parseUnitId() {
        val unitId = StepikUrlUtils.parseUnitId("?course=Курс-для-тестирования-плагина-Stepik-Union&unit=19749")
        assertEquals(19749, unitId)
    }

    @Test
    fun getCourseDescriptionIdIsZero() {
        val description = StepikUrlUtils.getCourseDescription(Course())
        assertEquals(zeroIdDescription, description)
    }

    companion object {
        private const val zeroIdDescription = "<b>A course does not selected.</b><br>" +
                "<ul><li>Select a course from a list.</li>" +
                "<li>Push on a refresh button if a course list is a empty.</li>" +
                "<li>Write a link to a course (example, https://stepik.org/187/) or a id of course.</li></ul>"
    }
}
