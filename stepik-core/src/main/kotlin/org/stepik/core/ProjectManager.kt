package org.stepik.core

import org.stepik.core.courseFormat.StudyNode

interface ProjectManager {
    var selected: StudyNode?
    var showHint: Boolean
    fun updateSelection()
    val isAdaptive: Boolean // FIXME
    fun getUuid(): String
    var defaultLang: SupportedLanguages?
    val version: Int
    var projectRoot: StudyNode?
    fun updateAdaptiveSelected() // FIXME
    var createdBy: Long
    fun refreshProjectFiles()
}
