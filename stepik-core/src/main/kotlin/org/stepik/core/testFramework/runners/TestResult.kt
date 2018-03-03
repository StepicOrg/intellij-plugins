package org.stepik.core.testFramework.runners

data class TestResult(val passed: Boolean, val actual: String, val cause: ExitCause, val errorString: String)

val TIME_LEFT = TestResult(false, "", ExitCause.TIME_LIMIT, "")

val NO_PROCESS = TestResult(false, "", ExitCause.NO_CREATE_PROCESS, "")
