package org.stepik.api.objects.steps

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyLessonArrayAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyStepArrayAdapter
import org.stepik.api.objects.ObjectsContainer


class Steps : ObjectsContainer<Step>() {
    @SerializedName("steps")
    @JsonAdapter(DefaultAsEmptyStepArrayAdapter::class, nullSafe = false)
    override var items = mutableListOf<Step>()

    override val itemClass
        get() = Step::class.java
}
