package org.stepik.core.courseFormat

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class StudyNodeTest {

    private lateinit var courseNode: CourseNode
    private lateinit var sectionNode: SectionNode
    private lateinit var lessonNode: LessonNode
    private lateinit var stepNode: StepNode

    @Before
    fun setUp() {
        courseNode = CourseNode()

        sectionNode = SectionNode()
        sectionNode.id = 1
        sectionNode.parent = courseNode


        lessonNode = LessonNode()
        lessonNode.id = 1
        lessonNode.parent = sectionNode

        stepNode = StepNode()
        stepNode.id = 1
        stepNode.parent = lessonNode

        courseNode.setChildren(listOf(sectionNode))
        sectionNode.setChildren(listOf(lessonNode))
        lessonNode.setChildren(listOf(stepNode))
    }

    @Test
    fun getCourseDirectory() {
        assertEquals("", courseNode.directory)
    }

    @Test
    fun getCoursePath() {
        assertEquals("", courseNode.path)
    }

    @Test
    fun getSectionDirectory() {
        assertEquals("section1", sectionNode.directory)
    }

    @Test
    fun getSectionPath() {
        assertEquals("section1", sectionNode.path)
    }

    @Test
    fun getLessonDirectory() {
        assertEquals("lesson1", lessonNode.directory)
    }

    @Test
    fun getLessonPath() {
        assertEquals("section1/lesson1", lessonNode.path)
    }

    @Test
    fun getStepDirectory() {
        assertEquals("step1", stepNode.directory)
    }

    @Test
    fun getStepPath() {
        assertEquals("section1/lesson1/step1", stepNode.path)
    }

    @Test
    fun getSectionDirectoryAfterChangeId() {
        sectionNode.id = 2
        assertEquals("section2", sectionNode.directory)
    }

    @Test
    fun getSectionPathAfterChangeId() {
        sectionNode.id = 2
        assertEquals("section2", sectionNode.path)
    }

    @Test
    fun getLessonDirectoryAfterChangeId() {
        lessonNode.id = 2
        assertEquals("lesson2", lessonNode.directory)
    }

    @Test
    fun getLessonPathAfterChangeId() {
        lessonNode.id = 2
        assertEquals("section1/lesson2", lessonNode.path)
    }

    @Test
    fun getStepDirectoryAfterChangeId() {
        stepNode.id = 2
        assertEquals("step2", stepNode.directory)
    }

    @Test
    fun getStepPathAfterChangeId() {
        stepNode.id = 2
        assertEquals("section1/lesson1/step2", stepNode.path)
    }

    @Test
    fun getLessonPathAfterChangeSectionId() {
        assertEquals("section1/lesson1", lessonNode.path)
        sectionNode.id = 2
        assertEquals("section2/lesson1", lessonNode.path)
    }

    @Test
    fun getStepPathAfterChangeLessonId() {
        assertEquals("section1/lesson1/step1", stepNode.path)
        lessonNode.id = 2
        assertEquals("section1/lesson2/step1", stepNode.path)
    }
}
