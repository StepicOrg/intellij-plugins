package org.stepik.core.courseFormat

enum class StudyStatus {
    UNCHECKED, SOLVED, FAILED, NEED_CHECK;


    companion object {

        fun of(status: String?): StudyStatus {
            return when (status?.toLowerCase()) {
                "correct", "solved" -> SOLVED
                "wrong", "failed" -> FAILED
                null -> NEED_CHECK
                else -> UNCHECKED
            }
        }
    }
}
