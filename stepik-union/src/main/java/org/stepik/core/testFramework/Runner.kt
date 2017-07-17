package org.stepik.core.testFramework

import com.intellij.execution.RunManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode

interface Runner {

    fun updateRunConfiguration(project: Project, stepNode: StepNode) {
        ApplicationManager.getApplication().invokeLater {
            RunManager.getInstance(project).selectedConfiguration = null
        }
    }

}
