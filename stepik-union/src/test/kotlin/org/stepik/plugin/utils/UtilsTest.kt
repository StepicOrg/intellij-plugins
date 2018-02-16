package org.stepik.plugin.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import org.stepik.api.objects.courses.Course


class UtilsTest {

    @Test
    @Throws(Exception::class)
    fun parseUnitId() {
        val unitId = Utils.parseUnitId("?course=Курс-для-тестирования-плагина-Stepik-Union&unit=19749")
        assertEquals(19749, unitId)
    }

    @Test
    @Throws(Exception::class)
    fun getCourseDescriptionIdIsZero() {
        val description = Utils.getCourseDescription(Course())
        assertEquals(zeroIdDescription, description)
    }

    companion object {
        private const val zeroIdDescription = "<b>A course does not selected.</b><br>" +
                "<ul><li>Select a course from a list.</li>" +
                "<li>Push on a refresh button if a course list is a empty.</li>" +
                "<li>Write a link to a course (example, https://stepik.org/187/) or a id of course.</li></ul>"
    }
}
