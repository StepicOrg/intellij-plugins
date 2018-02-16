package org.stepik.plugin.actions

import org.junit.Assert.assertEquals
import org.junit.Test
import org.stepik.core.actions.etaAsString
import org.stepik.core.actions.getShortcutText

class ActionUtilsTest {
    @Test
    @Throws(Exception::class)
    fun etaAsStringBig() {
        val result = etaAsString(2_156_484_515L)
        assertEquals("599 hours 1 minute 24 seconds", result)
    }

    @Test
    @Throws(Exception::class)
    fun etaAsStringLessThanHour() {
        val result = etaAsString(151_515L)
        assertEquals("2 minutes 31 seconds", result)
    }

    @Test
    @Throws(Exception::class)
    fun etaAsStringLessThanMinute() {
        val result = etaAsString(5_480L)
        assertEquals("5 seconds", result)
    }

    @Test
    @Throws(Exception::class)
    fun etaAsStringLessThanSecond() {
        val result = etaAsString(850L)
        assertEquals("", result)
    }

    @Test
    @Throws(Exception::class)
    fun etaAsStringNegative() {
        val result = etaAsString(-100L)
        assertEquals("", result)
    }

    @Test
    @Throws(Exception::class)
    fun etaAsStringZero() {
        val result = etaAsString(0)
        assertEquals("", result)
    }

    @Test
    @Throws(Exception::class)
    fun testGetShortcutText() {
        val result = getShortcutText("ctrl shift pressed ENTER")
        assertEquals("Ctrl+Shift+Enter", result)
    }
}
