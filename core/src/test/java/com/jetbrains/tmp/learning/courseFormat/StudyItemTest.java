package com.jetbrains.tmp.learning.courseFormat;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author meanmail
 */
public class StudyItemTest {

    private Course course;
    private Section section;
    private Lesson lesson;
    private Task task;

    @Before
    public void setUp() throws Exception {
        course = new Course();

        section = new Section();
        section.setIndex(1);
        section.setCourse(course);


        lesson = new Lesson();
        lesson.setIndex(1);
        lesson.setSection(section);

        task = new Task();
        task.setIndex(1);
        task.setLesson(lesson);

        course.addSection(section);
        section.addLesson(lesson);
        lesson.addTask(task);
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
    public void getTaskDirectory() throws Exception {
        assertEquals("task1", task.getDirectory());
    }

    @Test
    public void getTaskPath() throws Exception {
        assertEquals("/section1/lesson1/task1", task.getPath());
    }

    @Test
    public void getSectionDirectoryAfterChangeIndex() throws Exception {
        section.setIndex(2);
        assertEquals("section2", section.getDirectory());
    }

    @Test
    public void getSectionPathAfterChangeIndex() throws Exception {
        section.setIndex(2);
        assertEquals("/section2", section.getPath());
    }

    @Test
    public void getLessonDirectoryAfterChangeIndex() throws Exception {
        lesson.setIndex(2);
        assertEquals("lesson2", lesson.getDirectory());
    }

    @Test
    public void getLessonPathAfterChangeIndex() throws Exception {
        lesson.setIndex(2);
        assertEquals("/section1/lesson2", lesson.getPath());
    }

    @Test
    public void getTaskDirectoryAfterChangeIndex() throws Exception {
        task.setIndex(2);
        assertEquals("task2", task.getDirectory());
    }

    @Test
    public void getTaskPathAfterChangeIndex() throws Exception {
        task.setIndex(2);
        assertEquals("/section1/lesson1/task2", task.getPath());
    }

    @Test
    public void getLessonPathAfterChangeSectionIndex() throws Exception {
        assertEquals("/section1/lesson1", lesson.getPath());
        section.setIndex(2);
        assertEquals("/section2/lesson1", lesson.getPath());
    }

    @Test
    public void getTaskPathAfterChangeLessonIndex() throws Exception {
        assertEquals("/section1/lesson1/task1", task.getPath());
        lesson.setIndex(2);
        assertEquals("/section1/lesson2/task1", task.getPath());
    }
}