package org.stepik.plugin.actions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionUtilsTest {
    @Test
    public void etaAsStringBig() throws Exception {
        String result = ActionUtils.etaAsString(2_156_484_515L);
        assertEquals("599 hours 1 minute 24 seconds", result);
    }

    @Test
    public void etaAsStringLessThanHour() throws Exception {
        String result = ActionUtils.etaAsString(151_515L);
        assertEquals("2 minutes 31 seconds", result);
    }

    @Test
    public void etaAsStringLessThanMinute() throws Exception {
        String result = ActionUtils.etaAsString(5_480L);
        assertEquals("5 seconds", result);
    }

    @Test
    public void etaAsStringLessThanSecond() throws Exception {
        String result = ActionUtils.etaAsString(850L);
        assertEquals("", result);
    }

    @Test
    public void etaAsStringNegative() throws Exception {
        String result = ActionUtils.etaAsString(-100L);
        assertEquals("", result);
    }

    @Test
    public void etaAsStringZero() throws Exception {
        String result = ActionUtils.etaAsString(0);
        assertEquals("", result);
    }

    @Test
    public void getShortcutText() throws Exception {
        String result = ActionUtils.getShortcutText("ctrl shift pressed ENTER");
        assertEquals("Ctrl+Shift+Enter", result);
    }
}