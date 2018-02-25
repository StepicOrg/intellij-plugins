package org.stepik.api.objects.steps

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyStringArrayAdapter
import org.stepik.api.objects.ObjectsContainer


class Steps : ObjectsContainer<Step>() {
    @SerializedName("steps")
    @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
    override var items = mutableListOf<Step>()

    override val itemClass
        get() = Step::class.java
}
