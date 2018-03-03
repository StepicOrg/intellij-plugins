package org.stepik.api.objects.submissions

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptySubmissionArrayAdapter
import org.stepik.api.objects.ObjectsContainer


class Submissions : ObjectsContainer<Submission>() {
    @SerializedName("submissions")
    @JsonAdapter(DefaultAsEmptySubmissionArrayAdapter::class, nullSafe = false)
    override var items = mutableListOf<Submission>()

    override val itemClass
        get() = Submission::class.java
}
