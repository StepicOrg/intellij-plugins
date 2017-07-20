package org.stepik.core.testFramework.processes

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode

open class TestProcess(val project: Project, val stepNode: StepNode) {

    open fun start(): Process? = null
}
