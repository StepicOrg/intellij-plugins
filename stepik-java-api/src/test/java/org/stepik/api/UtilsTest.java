package org.stepik.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
public class UtilsTest {
    @Test
    public void mapToGetStringOneValue() throws Exception {
        String getString = Utils.mapToGetString("param", new String[]{"10"});

        assertEquals("param=10", getString);
    }

    @Test
    public void mapToGetStringManyValues() throws Exception {
        String getString = Utils.mapToGetString("param", new String[]{"10", "255", "value"});

        assertEquals("param=10&param=255&param=value", getString);
    }

    @Test
    public void mapToGetStringNoOneValues() throws Exception {
        String getString = Utils.mapToGetString("param", new String[0]);

        assertEquals("", getString);
    }

    @Test
    public void mapToGetStringNeedEncodeValues() throws Exception {
        String name = "\u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440";
        String[] value = new String[]{"\u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435"};
        String getString = Utils.mapToGetString(name, value);

        String expected;
        expected = "%D0%BF%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%D1%82%D1%80=%D0%B7%D0%BD%D0%B0%D1%87%D0%B5%D0%BD%D0%B8%D0%B5";

        assertEquals(expected, getString);
    }
}