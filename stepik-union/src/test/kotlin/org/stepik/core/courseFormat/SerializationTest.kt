package org.stepik.core.courseFormat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.stepik.api.objects.steps.Limit
import org.stepik.core.StepikProjectManager
import org.stepik.core.TestUtils
import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException


class SerializationTest {

    @Test
    @Throws(IOException::class,
            SAXException::class,
            ParserConfigurationException::class,
            InstantiationException::class,
            IllegalAccessException::class)
    fun serializeCourseNode() {
        val node = CourseNode()
        node.data
        serialize("CourseNode", node)
    }

    @Test
    @Throws(IOException::class, SAXException::class,
            ParserConfigurationException::class,
            InstantiationException::class,
            IllegalAccessException::class)
    fun serializeSectionNode() {
        val node = SectionNode()
        node.data
        val lessonNode = LessonNode()
        lessonNode.parent = node
        val data = lessonNode.data
        assertNotNull(data)
        data!!.lesson
        data.unit
        node.children.add(lessonNode)
        serialize("SectionNode", node)
    }

    @Test
    @Throws(IOException::class, SAXException::class,
            ParserConfigurationException::class,
            InstantiationException::class,
            IllegalAccessException::class)
    fun serializeLessonNode() {
        val node = LessonNode()
        val data = node.data
        assertNotNull(data)
        data!!.lesson
        data.unit
        serialize("LessonNode", node)
    }

    @Test
    @Throws(IOException::class, SAXException::class,
            ParserConfigurationException::class,
            InstantiationException::class,
            IllegalAccessException::class)
    fun serializeStepNode() {
        val node = StepNode()
        node.data
        node.id = 100
        val limit = Limit()
        limit.memory = 256
        limit.time = 8
        val data = node.data
        assertNotNull(data)
        data!!.block.options.limits["Java 8"] = limit
        serialize("StepNode", node)
    }

    @Throws(IOException::class)
    private fun serialize(name: String, node: StudyNode<*, *>) {
        val xs = StepikProjectManager.xStream

        val expected = TestUtils.readTextFile(SerializationTest::class.java, String.format("expected%s.xml", name))

        assertEquals(name, expected, xs.toXML(node))
    }
}
