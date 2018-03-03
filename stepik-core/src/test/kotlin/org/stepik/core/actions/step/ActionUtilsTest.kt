package org.stepik.core.actions.step

import org.junit.Assert.assertEquals
import org.junit.Test
import org.stepik.core.actions.etaAsString


class ActionUtilsTest {
    @Test
    fun etaAsStringOneSecond() {
        val etaString = etaAsString(1000)

        assertEquals("1 second", etaString)
    }

    @Test
    fun etaAsStringOneMinute() {
        val etaString = etaAsString((1000 * 60).toLong())

        assertEquals("1 minute", etaString)
    }

    @Test
    fun etaAsStringOneHour() {
        val etaString = etaAsString((1000 * 60 * 60).toLong())

        assertEquals("1 hour", etaString)
    }

    @Test
    fun etaAsStringMany() {
        val etaString = etaAsString(100500)

        assertEquals("1 minute 40 seconds", etaString)
    }

    @Test
    fun etaAsStringManyHour() {
        val etaString = etaAsString((42 * 42 * 42 * 1000).toLong())

        assertEquals("20 hours 34 minutes 48 seconds", etaString)
    }
}
