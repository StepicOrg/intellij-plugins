package org.stepik.core

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode

interface ProjectManager {
    fun getSelected(): StepNode?
    fun getSelected(project: Project): StepNode?
    fun getProjectRoot(): StepNode?
    fun isStepikProject(project: Project): Boolean
    val showHint: Boolean
}
