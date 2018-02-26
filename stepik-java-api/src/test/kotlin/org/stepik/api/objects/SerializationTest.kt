package org.stepik.api.objects

import org.junit.Test
import org.stepik.api.client.serialization.DefaultJsonConverter
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.lessons.Lesson
import org.stepik.api.objects.lessons.Lessons
import org.stepik.api.objects.steps.Sample
import org.stepik.api.objects.steps.Step
import org.stepik.api.objects.steps.Steps
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
                Sample()
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
                Sample()
        ).forEach {
            deserialize(it)
        }
    }

    private fun readJson(filename: String): String {
        return javaClass.getResource("serialization/$filename").readText().trim()
    }

    @Test
    fun lessons() {
        val json = "{\"meta\": {\"page\": 1, \"has_next\": false, \"has_previous\": false}, \"lessons\": [{\"id\": 50509, \"steps\": [202943], \"actions\": {}, \"tags\": [], \"required_tags\": [], \"progress\": \"76-50509\", \"subscriptions\": [\"31-76-50509\", \"30-76-50509\"], \"viewed_by\": 141, \"passed_by\": 118, \"time_to_complete\": 35, \"cover_url\": null, \"is_comments_enabled\": true, \"owner\": 1810113, \"language\": \"en\", \"is_featured\": false, \"is_public\": true, \"title\": \"Boolean type and logical operation - Q2\", \"slug\": \"Boolean-type-and-logical-operation-Q2-50509\", \"create_date\": \"2017-07-14T16:26:19Z\", \"update_date\": \"2017-12-19T13:15:22Z\", \"learners_group\": null, \"testers_group\": null, \"moderators_group\": null, \"teachers_group\": null, \"admins_group\": null, \"discussions_count\": 0, \"discussion_proxy\": \"76-50509-1\", \"discussion_threads\": [\"76-50509-1\"], \"epic_count\": 2, \"abuse_count\": 0, \"vote\": \"76-50509\", \"lti_consumer_key\": \"\", \"lti_secret_key\": \"\"}]}"
        assertNotNull(DefaultJsonConverter.fromJson(json, Lessons::class.java))
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
