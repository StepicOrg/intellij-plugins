package org.stepik.core.testFramework.processes

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode

open class TestProcess(val project: Project, val stepNode: StepNode, val mainFilePath: String) {

    open fun start(testClass: Boolean = false): Process? = null
}
