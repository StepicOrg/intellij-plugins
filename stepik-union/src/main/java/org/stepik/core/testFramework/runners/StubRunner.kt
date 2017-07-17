package org.stepik.core.testFramework.runners

import org.stepik.core.testFramework.Runner

class StubRunner : Runner {
    companion object {
        val instance = StubRunner()
    }
}
