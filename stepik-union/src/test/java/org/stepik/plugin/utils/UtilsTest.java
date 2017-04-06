package org.stepik.plugin.utils;

import org.junit.Test;
import org.stepik.api.objects.courses.Course;

import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
public class UtilsTest {
    private static final String zeroIdDescription = "<b>A course does not selected.</b><br>" +
            "<ul><li>Select a course from a list.</li>" +
            "<li>Push on a refresh button if a course list is a empty.</li>" +
            "<li>Write a link to a course (example, https://stepik.org/187/) or a id of course.</li></ul>";

    @Test
    public void parseUnitId() throws Exception {
        long unitId = Utils.parseUnitId("?course=Курс-для-тестирования-плагина-Stepik-Union&unit=19749");
        assertEquals(19749, unitId);
    }

    @Test
    public void getCourseDescriptionIdIsZero() throws Exception {
        String description = Utils.getCourseDescription(new Course());
        assertEquals(zeroIdDescription, description);
    }
}