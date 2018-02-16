package org.stepik.core.courseFormat


enum class StepType(val typeName: String) {
    UNKNOWN("unknown"),
    CODE("code"),
    TEXT("text"),
    VIDEO("video"),
    CHOICE("choice"),
    STRING("string"),
    SORTING("sorting"),
    MATCHING("matching"),
    NUMBER("number"),
    DATASET("dataset"),
    TABLE("table"),
    FILL_BLANKS("fill-blanks"),
    MATH("math"),
    FREE_ANSWER("free-answer");

    override fun toString(): String {
        return typeName
    }

    companion object {

        private val map: Map<String, StepType> by lazy {
            values().map { it.typeName to it }.toMap()
        }

        fun of(typeName: String): StepType {
            return map[typeName] ?: UNKNOWN
        }
    }
}
