package org.stepik.core.actions

import org.junit.Assert.assertEquals
import org.junit.Test

class ActionUtilsTest {
    @Test
    fun etaAsStringBig() {
        val result = etaAsString(2_156_484_515L)
        assertEquals("599 hours 1 minute 24 seconds", result)
    }

    @Test
    fun etaAsStringLessThanHour() {
        val result = etaAsString(151_515L)
        assertEquals("2 minutes 31 seconds", result)
    }

    @Test
    fun etaAsStringLessThanMinute() {
        val result = etaAsString(5_480L)
        assertEquals("5 seconds", result)
    }

    @Test
    fun etaAsStringLessThanSecond() {
        val result = etaAsString(850L)
        assertEquals("", result)
    }

    @Test
    fun etaAsStringNegative() {
        val result = etaAsString(-100L)
        assertEquals("", result)
    }

    @Test
    fun etaAsStringZero() {
        val result = etaAsString(0)
        assertEquals("", result)
    }

    @Test
    fun testGetShortcutText() {
        val result = getShortcutText("ctrl shift pressed ENTER")
        assertEquals("Ctrl+Shift+Enter", result)
    }
}
