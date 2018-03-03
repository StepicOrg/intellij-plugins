package org.stepik.api

import org.junit.Assert.assertEquals
import org.junit.Test
import org.stepik.api.Utils.mapToGetString


class UtilsTest {
    @Test
    fun mapToGetStringOneValue() {
        val getString = mapToGetString("param", arrayOf("10"))

        assertEquals("param=10", getString)
    }

    @Test
    fun mapToGetStringManyValues() {
        val getString = mapToGetString("param", arrayOf("10", "255", "value"))

        assertEquals("param=10&param=255&param=value", getString)
    }

    @Test
    fun mapToGetStringNoOneValues() {
        val getString = mapToGetString("param", arrayOf())

        assertEquals("", getString)
    }

    @Test
    fun mapToGetStringNeedEncodeValues() {
        val name = "\u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440"
        val value = arrayOf("\u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435")
        val getString = mapToGetString(name, value)

        val expected = "%D0%BF%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%D1%82%D1%80=%D0%B7%D0%BD%D0%B0%D1%87%D0%B5%D0%BD%D0%B8%D0%B5"

        assertEquals(expected, getString)
    }
}
