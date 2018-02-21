package org.stepik.core.courseFormat

enum class StudyStatus {
    UNCHECKED,
    FAILED,
    SOLVED;


    companion object {

        fun of(status: String?): StudyStatus {
            return when (status?.toLowerCase()) {
                "correct", "solved" -> SOLVED
                "wrong", "failed" -> FAILED
                else -> UNCHECKED
            }
        }
    }
}
