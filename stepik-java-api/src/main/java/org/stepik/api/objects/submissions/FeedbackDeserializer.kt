package org.stepik.api.objects.submissions

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken
import org.stepik.api.Utils
import java.lang.reflect.Type


class FeedbackDeserializer : JsonDeserializer<Feedback> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Feedback? {
        val feedback = Feedback()

        if (json is JsonPrimitive && json.isString) {
            feedback.message = json.asString
            return feedback
        }

        if (json !is JsonObject) {
            return feedback
        }

        val obj = json.asJsonObject

        val message = Utils.getString(obj, "message")
        if (message != null) {
            feedback.message = message
        }

        val data: MutableMap<String, Any> = context.deserialize(obj.getAsJsonObject("data"),
                object : TypeToken<MutableMap<String, Any>>() {}.type)
        feedback.data = data


        return feedback
    }
}
