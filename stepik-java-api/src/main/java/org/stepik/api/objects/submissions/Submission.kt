package org.stepik.api.objects.submissions

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyDoubleAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyIntAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyStringAdapter
import org.stepik.api.client.serialization.DefaultAsEpochDateAdapter
import org.stepik.api.objects.AbstractObject
import org.stepik.api.toDate
import java.time.Instant
import java.util.*

class Submission : AbstractObject() {
    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    var status: String = ""

    @JsonAdapter(DefaultAsEmptyDoubleAdapter::class, nullSafe = false)
    var score: Double = 0.0

    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    var hint: String = ""

    var feedback: Feedback = Feedback()

    @JsonAdapter(DefaultAsEpochDateAdapter::class, nullSafe = false)
    var time: Date = Instant.EPOCH.toDate()

    var reply: Reply = Reply()

    @SerializedName("reply_url")
    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    var replyUrl: String = ""

    @JsonAdapter(DefaultAsEmptyIntAdapter::class, nullSafe = false)
    var attempt: Int = 0

    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    var session: String = ""

    @JsonAdapter(DefaultAsEmptyDoubleAdapter::class, nullSafe = false)
    var eta: Double = 0.0
}
