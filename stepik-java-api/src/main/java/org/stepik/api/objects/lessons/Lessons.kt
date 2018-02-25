package org.stepik.api.objects.lessons

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyStringArrayAdapter
import org.stepik.api.objects.ObjectsContainer


class Lessons : ObjectsContainer<Lesson>() {
    @SerializedName("lessons")
    @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
    override var items = mutableListOf<Lesson>()

    override val itemClass
        get() = Lesson::class.java
}
