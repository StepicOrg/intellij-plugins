package org.stepik.plugin.actions.step;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
public class ActionUtilsTest {
    @Test
    public void etaAsStringOneSecond() throws Exception {
        String etaString = ActionUtils.etaAsString(1000);

        assertEquals("1 second", etaString);
    }

    @Test
    public void etaAsStringOneMinute() throws Exception {
        String etaString = ActionUtils.etaAsString(1000 * 60);

        assertEquals("1 minute", etaString);
    }

    @Test
    public void etaAsStringOneHour() throws Exception {
        String etaString = ActionUtils.etaAsString(1000 * 60 * 60);

        assertEquals("1 hour", etaString);
    }

    @Test
    public void etaAsStringMany() throws Exception {
        String etaString = ActionUtils.etaAsString(100500);

        assertEquals("1 minute 40 seconds", etaString);
    }

    @Test
    public void etaAsStringManyHour() throws Exception {
        String etaString = ActionUtils.etaAsString(42 * 42 * 42 * 1000);

        assertEquals("20 hours 34 minutes 48 seconds", etaString);
    }
}