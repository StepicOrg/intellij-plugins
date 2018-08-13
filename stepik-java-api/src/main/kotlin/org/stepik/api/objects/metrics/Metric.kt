package org.stepik.api.objects.metrics

class Metric {
    var name: String? = null
    var timestamp: Long? = null
    var tags: MutableMap<String, String>? = null
        get() {
            if (field == null) {
                field = mutableMapOf()
            }
            return field
        }
    var data: MutableMap<String, Any>? = null
        get() {
            if (field == null) {
                field = mutableMapOf()
            }
            return field
        }
    
    fun addData(key: String, value: Any): Metric {
        data!![key] = value
        return this
    }
    
    fun addTags(key: String, value: String): Metric {
        tags!![key] = value
        return this
    }
}
