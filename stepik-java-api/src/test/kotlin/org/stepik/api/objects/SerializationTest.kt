package org.stepik.api.objects

import org.junit.Test
import org.stepik.api.client.serialization.DefaultJsonConverter
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.lessons.Lesson
import org.stepik.api.objects.lessons.Lessons
import org.stepik.api.objects.steps.Sample
import org.stepik.api.objects.steps.Step
import org.stepik.api.objects.steps.Steps
import org.stepik.api.objects.submissions.Submission
import org.stepik.api.objects.submissions.Submissions
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class SerializationTest {

    private fun serialize(instance: Any) {
        val json = DefaultJsonConverter.toJson(instance, true)

        val expected = readJson("${instance::class.simpleName}_default.json")

        assertEquals(expected, json, instance::class.simpleName)
    }

    private fun deserialize(instance: Any) {
        val source = readJson("${instance::class.simpleName}_source.json")

        val deserialized = DefaultJsonConverter.fromJson(source, instance::class.java) ?: fail()

        deserialized.assertNotNullPropertiesIsNotNull()

        assertEquals(instance, deserialized, instance::class.simpleName)
    }

    @Test
    fun serializeTest() {
        listOf(
                Step(),
                Steps(),
                Lesson(),
                Lessons(),
                CompoundUnitLesson(),
                Sample(),
                Submission()
        ).forEach {
            serialize(it)
        }
    }

    @Test
    fun deserializeTest() {
        listOf(
                Step(),
                Steps(),
                Lesson(),
                Lessons(),
                CompoundUnitLesson(),
                Sample(),
                Submission()
        ).forEach {
            deserialize(it)
        }
    }

    private fun readJson(filename: String): String {
        return javaClass.getResource("serialization/$filename").readText().trim()
    }

    @Test
    fun lessons() {
        val json = readJson("submissions.json")
        assertNotNull(DefaultJsonConverter.fromJson(json, Submissions::class.java))
    }
}

fun <T : Any> T.assertNotNullPropertiesIsNotNull() {
    this::class.memberProperties.filter {
        it.visibility == KVisibility.PUBLIC && !it.returnType.isMarkedNullable && it.javaField != null
    }
            .forEach {
                val value = it.getter.call(this)
                assertNotNull(value, "Property ${it.name}. Expected not null, but it is null.")
            }
}
