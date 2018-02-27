package org.stepik.core.testFramework.runners

enum class ExitCause {
    CORRECT,
    WRONG,
    TIME_LIMIT,
    NO_CREATE_PROCESS;

    companion object {

        fun of(value: Boolean): ExitCause {
            return if (value) CORRECT else WRONG
        }

    }
}
