package org.stepik.api.objects

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyStringAdapter
import org.stepik.api.client.serialization.DefaultAsEpochDateAdapter
import org.stepik.api.client.serialization.DefaultJsonConverter
import java.time.Instant
import java.util.*


open class StudyObject : AbstractObject() {

    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    open var title: String = ""

    @SerializedName("is_adaptive")
    open var isAdaptive: Boolean = false

    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    open var description: String = ""

    open var position: Int = 0

    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    open var progress: String = ""

    @SerializedName("create_date")
    @JsonAdapter(DefaultAsEpochDateAdapter::class, nullSafe = false)
    open var createDate: Date = Date.from(Instant.EPOCH)

    @SerializedName("update_date")
    @JsonAdapter(DefaultAsEpochDateAdapter::class, nullSafe = false)
    open var updateDate: Date = Date.from(Instant.EPOCH)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StudyObject

        if (title != other.title) return false
        if (isAdaptive != other.isAdaptive) return false
        if (description != other.description) return false
        if (position != other.position) return false
        if (progress != other.progress) return false
        if (createDate != other.createDate) return false
        if (updateDate != other.updateDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + isAdaptive.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + position
        result = 31 * result + progress.hashCode()
        result = 31 * result + createDate.hashCode()
        result = 31 * result + updateDate.hashCode()
        return result
    }

    override fun toString(): String {
        return DefaultJsonConverter.toJson(this, true)
    }
}
