package org.stepik.core.serialization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.stepik.core.SupportedLanguages
import org.stepik.core.SupportedLanguages.INVALID
import org.stepik.core.SupportedLanguages.JAVA8
import org.stepik.core.SupportedLanguages.PYTHON3

class SupportedLanguagesConverterTest {
    val converter = SupportedLanguagesConverter()

    @Test
    fun testToString() {
        assertEquals(JAVA8.title, converter.toString(JAVA8))
    }

    @Test
    fun fromString() {
        assertEquals(PYTHON3, converter.fromString("Python 3"))
        assertEquals(INVALID, converter.fromString("invalid language"))
    }

    @Test
    fun canConvert() {
        assertTrue(converter.canConvert(SupportedLanguages::class.java))
    }

    @Test
    fun canNotConvert() {
        assertFalse(converter.canConvert(String::class.java))
    }

}