package com.jetbrains.tmp.learning.courseFormat;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
public class StudyNodeTest {

    private CourseNode courseNode;
    private SectionNode sectionNode;
    private LessonNode lessonNode;
    private StepNode stepNode;

    @Before
    public void setUp() throws Exception {
        courseNode = new CourseNode();

        sectionNode = new SectionNode();
        sectionNode.setId(1);
        sectionNode.setParent(courseNode);


        lessonNode = new LessonNode();
        lessonNode.setId(1);
        lessonNode.setParent(sectionNode);

        stepNode = new StepNode();
        stepNode.setId(1);
        stepNode.setParent(lessonNode);

        courseNode.getChildren().add(sectionNode);
        sectionNode.getChildren().add(lessonNode);
        lessonNode.getChildren().add(stepNode);
    }

    @Test
    public void getCourseDirectory() throws Exception {
        assertEquals("", courseNode.getDirectory());
    }

    @Test
    public void getCoursePath() throws Exception {
        assertEquals("", courseNode.getPath());
    }

    @Test
    public void getSectionDirectory() throws Exception {
        assertEquals("section1", sectionNode.getDirectory());
    }

    @Test
    public void getSectionPath() throws Exception {
        assertEquals("section1", sectionNode.getPath());
    }

    @Test
    public void getLessonDirectory() throws Exception {
        assertEquals("lesson1", lessonNode.getDirectory());
    }

    @Test
    public void getLessonPath() throws Exception {
        assertEquals("section1/lesson1", lessonNode.getPath());
    }

    @Test
    public void getStepDirectory() throws Exception {
        assertEquals("step1", stepNode.getDirectory());
    }

    @Test
    public void getStepPath() throws Exception {
        assertEquals("section1/lesson1/step1", stepNode.getPath());
    }

    @Test
    public void getSectionDirectoryAfterChangeId() throws Exception {
        sectionNode.setId(2);
        assertEquals("section2", sectionNode.getDirectory());
    }

    @Test
    public void getSectionPathAfterChangeId() throws Exception {
        sectionNode.setId(2);
        assertEquals("section2", sectionNode.getPath());
    }

    @Test
    public void getLessonDirectoryAfterChangeId() throws Exception {
        lessonNode.setId(2);
        assertEquals("lesson2", lessonNode.getDirectory());
    }

    @Test
    public void getLessonPathAfterChangeId() throws Exception {
        lessonNode.setId(2);
        assertEquals("section1/lesson2", lessonNode.getPath());
    }

    @Test
    public void getStepDirectoryAfterChangeId() throws Exception {
        stepNode.setId(2);
        assertEquals("step2", stepNode.getDirectory());
    }

    @Test
    public void getStepPathAfterChangeId() throws Exception {
        stepNode.setId(2);
        assertEquals("section1/lesson1/step2", stepNode.getPath());
    }

    @Test
    public void getLessonPathAfterChangeSectionId() throws Exception {
        assertEquals("section1/lesson1", lessonNode.getPath());
        sectionNode.setId(2);
        assertEquals("section2/lesson1", lessonNode.getPath());
    }

    @Test
    public void getStepPathAfterChangeLessonId() throws Exception {
        assertEquals("section1/lesson1/step1", stepNode.getPath());
        lessonNode.setId(2);
        assertEquals("section1/lesson2/step1", stepNode.getPath());
    }
}