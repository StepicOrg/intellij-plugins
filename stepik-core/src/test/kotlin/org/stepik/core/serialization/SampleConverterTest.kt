package org.stepik.core.serialization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.stepik.api.objects.steps.Sample


class SampleConverterTest {
    private var sampleConverter: SampleConverter? = null

    @Before
    fun setUp() {
        sampleConverter = SampleConverter()
    }

    @Test
    fun toStringTest() {
        val sample = Sample()
        sample.setInput("1 2 3")
        sample.setOutput("123")

        val sampleString = sampleConverter!!.toString(sample)

        assertEquals("<input>1 2 3</input><output>123</output>", sampleString)
    }

    @Test
    fun fromStringTest() {
        val string = "<input>1 2 3</input><output>123</output>"

        val sample = sampleConverter!!.fromString(string)

        val expected = Sample()
        expected.setInput("1 2 3")
        expected.setOutput("123")

        assertEquals(expected, sample)
    }

    @Test
    fun canConvert() {
        val value = sampleConverter!!.canConvert(Sample::class.java)
        assertTrue(value)
    }

}
