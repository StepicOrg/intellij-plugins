package org.stepik.core.testFramework

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode

interface TestRunner {

    fun updateRunConfiguration(project: Project, stepNode: StepNode) {
    }

}
