package org.stepik.api

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

object Utils {
    private val logger = LoggerFactory.getLogger(Utils::class.java)
    
    val timeISOFormat by lazy {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val tz = TimeZone.getTimeZone("UTC")
        format.timeZone = tz
        return@lazy format
    }
    
    fun mapToGetString(name: String, values: List<String?>): String {
        val encodedName = encode(name)
        return values.filterNotNull()
                .joinToString("&") { "$encodedName=${encode(it)}" }
    }
    
    private fun encode(value: String): String {
        return try {
            URLEncoder.encode(value, UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            value
        }
    }
    
    fun readFile(file: File): String? {
        return try {
            file.readLines(charset = UTF_8)
                    .reduce { line, text -> text + line }
        } catch (e: IOException) {
            logger.warn("Failed reading a file: {}\n{}", file, e)
            null
        }
    }
    
    fun getStringList(obj: JsonObject, fieldName: String): List<String>? {
        return getList(obj, fieldName) { it.asString }
    }
    
    fun <T> getList(obj: JsonObject, fieldName: String, getter: (JsonElement) -> T): List<T>? {
        if (obj.has(fieldName) && obj.get(fieldName).isJsonArray) {
            return getJsonArray(obj, fieldName)?.map(getter)
        }
        return null
    }
    
    fun getJsonArray(json: JsonObject, memberName: String): JsonArray? {
        if (json.has(memberName) && json.get(memberName).isJsonArray) {
            return json.getAsJsonArray(memberName)
        }
        return null
    }
    
    private fun getPrimitive(json: JsonObject, memberName: String): JsonPrimitive? {
        val element = json.get(memberName) ?: return null
        
        if (element.isJsonPrimitive) {
            return element.asJsonPrimitive
        }
        return null
    }
    
    fun getString(json: JsonObject, memberName: String): String? {
        val primitive = getPrimitive(json, memberName) ?: return null
        
        if (primitive.isString) {
            return primitive.asString
        }
        return null
    }
    
    fun getBoolean(json: JsonObject, memberName: String): Boolean? {
        val primitive = getPrimitive(json, memberName) ?: return null
        
        if (primitive.isBoolean) {
            return primitive.asBoolean
        }
        return null
    }
    
    fun cleanString(string: String) =
            string.replace("[\\u0000-\\u0008\\u000b\\u000c\\u000e-\\u001f]".toRegex(), "")
}

fun Instant.toDate(): Date {
    return Date.from(this)
}

fun Date?.toIsoFormat(): String? {
    if (this == null || this == Instant.EPOCH.toDate()) {
        return null
    }
    return Utils.timeISOFormat.format(this)
}

fun String?.toDate(): Date {
    return try {
        Utils.timeISOFormat.parse(this)
    } catch (e: Exception) {
        Instant.EPOCH.toDate()
    }
}
