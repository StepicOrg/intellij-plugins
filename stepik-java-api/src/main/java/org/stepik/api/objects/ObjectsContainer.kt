package org.stepik.api.objects

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyMetaAdapter
import org.stepik.api.client.serialization.DefaultJsonConverter
import java.util.*


abstract class ObjectsContainer<T> : Iterable<T> {
    @SerializedName("meta")
    @JsonAdapter(DefaultAsEmptyMetaAdapter::class, nullSafe = false)
    var meta: Meta = Meta()

    val count: Int
        inline get() = items.size

    val isEmpty: Boolean
        inline get() = items.isEmpty()

    val isNotEmpty: Boolean
        inline get() = items.isNotEmpty()

    abstract val items: MutableList<T>

    abstract val itemClass: Class<T>

    fun first(): T {
        return items.first()
    }

    fun firstOrDefault(default: T): T {
        return items.firstOrNull() ?: default
    }

    operator fun get(index: Int): T {
        return items[index]
    }

    override fun iterator(): Iterator<T> {
        return items.iterator()
    }

    override fun spliterator(): Spliterator<T> {
        return items.spliterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjectsContainer<*>

        if (meta != other.meta) return false
        if (items != other.items) return false
        if (itemClass != other.itemClass) return false

        return true
    }

    override fun hashCode(): Int {
        var result = meta.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + itemClass.hashCode()
        return result
    }

    override fun toString(): String {
        return DefaultJsonConverter.toJson(this, true)
    }
}
