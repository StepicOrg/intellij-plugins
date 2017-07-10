package org.stepik.core.testFramework.runners

import org.stepik.core.testFramework.TestRunner

class StubTestRunner : TestRunner {
    companion object {
        val instance = StubTestRunner()
    }
}
