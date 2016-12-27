package com.jetbrains.tmp.learning.courseFormat;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
public class StudyItemTest {

    private Course course;
    private Section section;
    private Lesson lesson;
    private Step step;

    @Before
    public void setUp() throws Exception {
        course = new Course();

        section = new Section();
        section.setPosition(1);
        section.setId(1);
        section.setCourse(course);


        lesson = new Lesson();
        lesson.setPosition(1);
        lesson.setId(1);
        lesson.setSection(section);

        step = new Step();
        step.setPosition(1);
        step.setId(1);
        step.setLesson(lesson);

        course.addSection(section);
        section.addLesson(lesson);
        lesson.addStep(step);
    }

    @Test
    public void getCourseDirectory() throws Exception {
        assertEquals("", course.getDirectory());
    }

    @Test
    public void getCoursePath() throws Exception {
        assertEquals("", course.getPath());
    }

    @Test
    public void getSectionDirectory() throws Exception {
        assertEquals("section1", section.getDirectory());
    }

    @Test
    public void getSectionPath() throws Exception {
        assertEquals("/section1", section.getPath());
    }

    @Test
    public void getLessonDirectory() throws Exception {
        assertEquals("lesson1", lesson.getDirectory());
    }

    @Test
    public void getLessonPath() throws Exception {
        assertEquals("/section1/lesson1", lesson.getPath());
    }

    @Test
    public void getStepDirectory() throws Exception {
        assertEquals("step1", step.getDirectory());
    }

    @Test
    public void getStepPath() throws Exception {
        assertEquals("/section1/lesson1/step1", step.getPath());
    }

    @Test
    public void getSectionDirectoryAfterChangeId() throws Exception {
        section.setId(2);
        assertEquals("section2", section.getDirectory());
    }

    @Test
    public void getSectionPathAfterChangeId() throws Exception {
        section.setId(2);
        assertEquals("/section2", section.getPath());
    }

    @Test
    public void getLessonDirectoryAfterChangeId() throws Exception {
        lesson.setId(2);
        assertEquals("lesson2", lesson.getDirectory());
    }

    @Test
    public void getLessonPathAfterChangeId() throws Exception {
        lesson.setId(2);
        assertEquals("/section1/lesson2", lesson.getPath());
    }

    @Test
    public void getStepDirectoryAfterChangeId() throws Exception {
        step.setId(2);
        assertEquals("step2", step.getDirectory());
    }

    @Test
    public void getStepPathAfterChangeId() throws Exception {
        step.setId(2);
        assertEquals("/section1/lesson1/step2", step.getPath());
    }

    @Test
    public void getLessonPathAfterChangeSectionId() throws Exception {
        assertEquals("/section1/lesson1", lesson.getPath());
        section.setId(2);
        assertEquals("/section2/lesson1", lesson.getPath());
    }

    @Test
    public void getStepPathAfterChangeLessonId() throws Exception {
        assertEquals("/section1/lesson1/step1", step.getPath());
        lesson.setId(2);
        assertEquals("/section1/lesson2/step1", step.getPath());
    }
}