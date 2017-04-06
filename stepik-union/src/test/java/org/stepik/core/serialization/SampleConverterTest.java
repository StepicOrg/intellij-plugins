package org.stepik.core.serialization;

import org.junit.Before;
import org.junit.Test;
import org.stepik.api.objects.steps.Sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author meanmail
 */
public class SampleConverterTest {
    private SampleConverter sampleConverter;

    @Before
    public void setUp() {
        sampleConverter = new SampleConverter();
    }

    @Test
    public void toStringTest() throws Exception {
        Sample sample = new Sample();
        sample.setInput("1 2 3");
        sample.setOutput("123");

        String sampleString = sampleConverter.toString(sample);

        assertEquals("<input>1 2 3</input><output>123</output>", sampleString);
    }

    @Test
    public void fromStringTest() throws Exception {
        String string = "<input>1 2 3</input><output>123</output>";

        Object sample = sampleConverter.fromString(string);

        Sample expected = new Sample();
        expected.setInput("1 2 3");
        expected.setOutput("123");

        assertEquals(expected, sample);
    }

    @Test
    public void canConvert() throws Exception {
        boolean value = sampleConverter.canConvert(Sample.class);
        assertTrue(value);
    }

}