package org.hyperskill.api.objects.lesson

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.hyperskill.api.serializations.DefaultAsEmptyHSLessonArrayAdapter
import org.stepik.api.objects.ObjectsContainer

class HSLessons : ObjectsContainer<HSLesson>() {
    @SerializedName("lessons")
    @JsonAdapter(DefaultAsEmptyHSLessonArrayAdapter::class, nullSafe = false)
    override var items = mutableListOf<HSLesson>()
    
    override val itemClass
        get() = HSLesson::class.java
}
