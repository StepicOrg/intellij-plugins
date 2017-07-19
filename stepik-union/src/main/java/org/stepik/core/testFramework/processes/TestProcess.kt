package org.stepik.core.testFramework.processes

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode

abstract class TestProcess(val project: Project, val stepNode: StepNode) {

    abstract fun start(): Process?
}