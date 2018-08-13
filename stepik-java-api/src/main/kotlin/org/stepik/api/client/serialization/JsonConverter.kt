package org.stepik.api.client.serialization

interface JsonConverter {
    fun <T> fromJson(json: String?, clazz: Class<T>): T?
    
    fun toJson(any: Any?, pretty: Boolean = false): String
}
