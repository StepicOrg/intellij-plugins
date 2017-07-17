package org.stepik.core.testFramework

import com.intellij.execution.RunManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode

interface Runner {

    fun updateRunConfiguration(project: Project, stepNode: StepNode) {
        val runManager = RunManager.getInstance(project)
        ApplicationManager.getApplication().invokeLater { runManager.selectedConfiguration = null }
    }

}
