package org.stepik.core

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.projectView.ProjectTreeMode

interface ProjectManager {

    var selected: StudyNode?

    var showHint: Boolean

    fun updateSelection()

    val isAdaptive: Boolean // FIXME Plugin-specific

    val projectTreeMode: ProjectTreeMode

    fun getUuid(): String

    var defaultLang: SupportedLanguages?

    val version: Int

    var projectRoot: StudyNode?

    fun updateAdaptiveSelected() // FIXME Plugin-specific

    var createdBy: Long

    fun refreshProjectFiles()

    fun getConfigurator(project: Project): StudyPluginConfigurator?
}
