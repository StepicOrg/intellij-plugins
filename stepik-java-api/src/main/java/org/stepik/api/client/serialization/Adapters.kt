package org.stepik.api.client.serialization

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.stepik.api.objects.Meta
import org.stepik.api.objects.lessons.Lesson
import org.stepik.api.objects.steps.Step
import org.stepik.api.toDate
import org.stepik.api.toIsoFormat
import java.util.*

class DefaultAsEpochDateAdapter : TypeAdapter<Date>() {
    override fun write(out: JsonWriter, value: Date?) {
        val date = value.toIsoFormat()
        if (date == null) {
            out.nullValue()
        } else {
            out.value(date)
        }
    }

    override fun read(input: JsonReader): Date {
        return if (input.peek() === JsonToken.NULL) {
            input.skipValue()
            ""
        } else {
            input.nextString()
        }.toDate()
    }

}

class DefaultAsEmptyStringAdapter : TypeAdapter<String>() {
    override fun write(out: JsonWriter, value: String?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    override fun read(input: JsonReader): String {
        if (input.peek() === JsonToken.NULL) {
            input.skipValue()
            return ""
        }
        return input.nextString()
    }

}

private fun <T> getAdapter(clazz: Class<T>): TypeAdapter<T> {
    return DefaultJsonConverter.gson.getAdapter(clazz)
}

private fun <T> write(out: JsonWriter, adapter : TypeAdapter<T>, value: List<T>?) {
    if (value == null) {
        out.nullValue()
        return
    }

    out.beginArray()
    value.forEach {
        adapter.write(out, it)
    }
    out.endArray()
}

private fun <T> read(input: JsonReader, adapter : TypeAdapter<T>): List<T> {
    if (input.peek() === JsonToken.NULL) {
        input.skipValue()
        return emptyList()
    }

    val list = mutableListOf<T>()
    input.beginArray()
    while (input.hasNext()) {
        list.add(adapter.read(input))
    }
    input.endArray()

    return list
}

class DefaultAsEmptyStringArrayAdapter : TypeAdapter<List<String>>() {
    private val adapter = getAdapter(String::class.java)

    override fun write(out: JsonWriter, value: List<String>?) {
        write(out, adapter, value)
    }

    override fun read(input: JsonReader): List<String> {
        return read(input, adapter)
    }

}

class DefaultAsEmptyIntArrayAdapter : TypeAdapter<List<Int>>() {
    private val adapter = getAdapter(Int::class.java)

    override fun write(out: JsonWriter, value: List<Int>?) {
        write(out, adapter, value)
    }

    override fun read(input: JsonReader): List<Int> {
        return read(input, adapter)
    }
}

class DefaultAsEmptyLongArrayAdapter : TypeAdapter<List<Long>>() {
    private val adapter = getAdapter(Long::class.java)

    override fun write(out: JsonWriter, value: List<Long>?) {
        write(out, adapter, value)
    }

    override fun read(input: JsonReader): List<Long> {
        return read(input, adapter)
    }
}

class DefaultAsEmptyLessonArrayAdapter : TypeAdapter<List<Lesson>>() {
    private val adapter = getAdapter(Lesson::class.java)

    override fun write(out: JsonWriter, value: List<Lesson>?) {
        write(out, adapter, value)
    }

    override fun read(input: JsonReader): List<Lesson> {
        return read(input, adapter)
    }
}

class DefaultAsEmptyStepArrayAdapter : TypeAdapter<List<Step>>() {
    private val adapter = getAdapter(Step::class.java)

    override fun write(out: JsonWriter, value: List<Step>?) {
        write(out, adapter, value)
    }

    override fun read(input: JsonReader): List<Step> {
        return read(input, adapter)
    }
}

class DefaultAsEmptyStringStringMapAdapter : TypeAdapter<Map<String, String>>() {
    override fun write(out: JsonWriter, value: Map<String, String>?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.beginObject()
        value.forEach {
            out.name(it.key)
            out.value(it.value)
        }
        out.endObject()
    }

    override fun read(input: JsonReader): Map<String, String> {
        if (input.peek() === JsonToken.NULL) {
            input.skipValue()
            return emptyMap()
        }
        val map = mutableMapOf<String, String>()

        input.beginObject()
        while (input.hasNext()) {
            val key = input.nextName()
            val value = input.nextString()
            map[key] = value
        }
        input.endObject()

        return map
    }

}

class DefaultAsEmptyMetaAdapter : TypeAdapter<Meta>() {
    override fun write(out: JsonWriter, value: Meta?) {
        if (value == null) {
            out.nullValue()
            return
        }

        out.beginObject()
        out.name("page")
        out.value(value.page)
        out.name("has_next")
        out.value(value.hasNext)
        out.name("has_previous")
        out.value(value.hasPrevious)
        out.endObject()
    }

    override fun read(input: JsonReader): Meta {
        val meta = Meta()
        if (input.peek() === JsonToken.NULL) {
            input.skipValue()
            return meta
        }

        input.beginObject()
        while (input.hasNext()) {
            val key = input.nextName()
            when (key) {
                "page" -> meta.page = input.nextInt()
                "has_next" -> meta.hasNext = input.nextBoolean()
                "has_previous" -> meta.hasNext = input.nextBoolean()
            }
        }
        input.endObject()

        return meta
    }
}
