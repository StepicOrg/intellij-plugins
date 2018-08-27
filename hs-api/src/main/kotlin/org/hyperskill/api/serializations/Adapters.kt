package org.hyperskill.api.serializations

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.hyperskill.api.objects.lesson.HSLesson
import org.stepik.api.client.serialization.getAdapter
import org.stepik.api.client.serialization.readArray
import org.stepik.api.client.serialization.writeArray

class DefaultAsEmptyHSLessonArrayAdapter : TypeAdapter<List<HSLesson>>() {
    private val adapter = getAdapter(HSLesson::class.java)
    
    override fun write(out: JsonWriter, value: List<HSLesson>?) {
        writeArray(out, adapter, value)
    }
    
    override fun read(input: JsonReader): List<HSLesson> {
        return readArray(input, adapter)
    }
}
