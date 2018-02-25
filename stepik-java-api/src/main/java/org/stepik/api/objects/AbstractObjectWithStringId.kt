package org.stepik.api.objects

import com.google.gson.annotations.JsonAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyStringAdapter


open class AbstractObjectWithStringId {
    @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
    var id: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractObjectWithStringId

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}
