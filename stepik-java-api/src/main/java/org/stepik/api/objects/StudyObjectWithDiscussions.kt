package org.stepik.api.objects

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyStringAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyStringArrayAdapter

open class StudyObjectWithDiscussions : StudyObject() {
    @SerializedName("discussions_count")
    var discussionsCount = 0

    @SerializedName("discussion_proxy")
    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    var discussionProxy: String? = ""

    @SerializedName("discussion_threads")
    @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
    var discussionThreads = emptyList<String>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as StudyObjectWithDiscussions

        if (discussionsCount != other.discussionsCount) return false
        if (discussionProxy != other.discussionProxy) return false
        if (discussionThreads != other.discussionThreads) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + discussionsCount
        result = 31 * result + (discussionProxy?.hashCode() ?: 0)
        result = 31 * result + discussionThreads.hashCode()
        return result
    }

}
