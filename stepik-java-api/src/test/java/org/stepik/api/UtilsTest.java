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
        String getString = Utils.mapToGetString("param", new String[]{"10", "255", "100"});

        assertEquals("param=10&param=255&param=100", getString);
    }
}