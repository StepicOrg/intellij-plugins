package org.stepik.core.courseFormat

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class StudyNodeTest {

    private var courseNode: CourseNode? = null
    private var sectionNode: SectionNode? = null
    private var lessonNode: LessonNode? = null
    private var stepNode: StepNode? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        courseNode = CourseNode()

        sectionNode = SectionNode()
        sectionNode!!.id = 1
        sectionNode!!.parent = courseNode


        lessonNode = LessonNode()
        lessonNode!!.id = 1
        lessonNode!!.parent = sectionNode

        stepNode = StepNode()
        stepNode!!.id = 1
        stepNode!!.parent = lessonNode

        courseNode!!.children.add(sectionNode)
        sectionNode!!.children.add(lessonNode)
        lessonNode!!.children.add(stepNode)
    }

    @Test
    @Throws(Exception::class)
    fun getCourseDirectory() {
        assertEquals("", courseNode!!.directory)
    }

    @Test
    @Throws(Exception::class)
    fun getCoursePath() {
        assertEquals("", courseNode!!.path)
    }

    @Test
    @Throws(Exception::class)
    fun getSectionDirectory() {
        assertEquals("section1", sectionNode!!.directory)
    }

    @Test
    @Throws(Exception::class)
    fun getSectionPath() {
        assertEquals("section1", sectionNode!!.path)
    }

    @Test
    @Throws(Exception::class)
    fun getLessonDirectory() {
        assertEquals("lesson1", lessonNode!!.directory)
    }

    @Test
    @Throws(Exception::class)
    fun getLessonPath() {
        assertEquals("section1/lesson1", lessonNode!!.path)
    }

    @Test
    @Throws(Exception::class)
    fun getStepDirectory() {
        assertEquals("step1", stepNode!!.directory)
    }

    @Test
    @Throws(Exception::class)
    fun getStepPath() {
        assertEquals("section1/lesson1/step1", stepNode!!.path)
    }

    @Test
    @Throws(Exception::class)
    fun getSectionDirectoryAfterChangeId() {
        sectionNode!!.id = 2
        assertEquals("section2", sectionNode!!.directory)
    }

    @Test
    @Throws(Exception::class)
    fun getSectionPathAfterChangeId() {
        sectionNode!!.id = 2
        assertEquals("section2", sectionNode!!.path)
    }

    @Test
    @Throws(Exception::class)
    fun getLessonDirectoryAfterChangeId() {
        lessonNode!!.id = 2
        assertEquals("lesson2", lessonNode!!.directory)
    }

    @Test
    @Throws(Exception::class)
    fun getLessonPathAfterChangeId() {
        lessonNode!!.id = 2
        assertEquals("section1/lesson2", lessonNode!!.path)
    }

    @Test
    @Throws(Exception::class)
    fun getStepDirectoryAfterChangeId() {
        stepNode!!.id = 2
        assertEquals("step2", stepNode!!.directory)
    }

    @Test
    @Throws(Exception::class)
    fun getStepPathAfterChangeId() {
        stepNode!!.id = 2
        assertEquals("section1/lesson1/step2", stepNode!!.path)
    }

    @Test
    @Throws(Exception::class)
    fun getLessonPathAfterChangeSectionId() {
        assertEquals("section1/lesson1", lessonNode!!.path)
        sectionNode!!.id = 2
        assertEquals("section2/lesson1", lessonNode!!.path)
    }

    @Test
    @Throws(Exception::class)
    fun getStepPathAfterChangeLessonId() {
        assertEquals("section1/lesson1/step1", stepNode!!.path)
        lessonNode!!.id = 2
        assertEquals("section1/lesson2/step1", stepNode!!.path)
    }
}
