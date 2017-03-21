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
    private static final String adaptiveDescription = "Test" +
            "<p style='font-weight: bold;'>This course is adaptive.<br>" +
            "Sorry, but we don't support adaptive courses yet</p>";

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

    @Test
    public void getCourseDescriptionIsAdaptive() throws Exception {
        Course course = new Course();
        course.setId(1);
        course.setDescription("Test");
        course.setAdaptive(true);
        String description = Utils.getCourseDescription(course);
        assertEquals(adaptiveDescription, description);
    }
}