package org.stepik.core.courseFormat

enum class StudyStatus {
    UNCHECKED, SOLVED, FAILED, NEED_CHECK;


    companion object {

        fun of(status: String?): StudyStatus {
            val myStatus: String = status ?: return NEED_CHECK

            return when (myStatus.toLowerCase()) {
                "correct", "solved" -> SOLVED
                "wrong", "failed" -> FAILED
                else -> UNCHECKED
            }
        }
    }
}
