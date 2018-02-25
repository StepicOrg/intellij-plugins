package org.stepik.core.courseFormat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.steps.BlockView
import org.stepik.api.objects.steps.Limit
import org.stepik.api.objects.steps.Step
import org.stepik.core.TestUtils.readTextFile
import org.stepik.core.serialization.SerializationUtils.xStream


class SerializationTest {

    @Test
    fun serializeCourseNode() {
        val node = CourseNode()
        node.data
        serialize("CourseNode", node)
    }

    @Test
    fun serializeSectionNode() {
        val node = SectionNode()
        node.data
        val lessonNode = LessonNode()
        lessonNode.parent = node
        val data = lessonNode.data as CompoundUnitLesson
        assertNotNull(data)
        data.lesson
        data.unit
        node.setChildren(listOf(lessonNode))
        serialize("SectionNode", node)
    }

    @Test
    fun serializeLessonNode() {
        val node = LessonNode()
        val data = node.data as CompoundUnitLesson
        assertNotNull(data)
        data.lesson
        data.unit
        serialize("LessonNode", node)
    }

    @Test
    fun serializeStepNode() {
        val node = StepNode()
        node.data
        node.id = 100
        val limit = Limit()
        limit.memory = 256
        limit.time = 8
        val data = node.data as Step
        assertNotNull(data)
        data.block = BlockView().apply {
            options.limits["Java 8"] = limit
        }
        serialize("StepNode", node)
    }

    private fun serialize(name: String, node: StudyNode) {
        val xs = xStream

        val expected = readTextFile(SerializationTest::class.java, String.format("expected%s.xml", name))

        assertEquals(name, expected, xs.toXML(node))
    }
}
