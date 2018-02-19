package org.stepik.plugin.projectWizard.ui

import org.stepik.api.objects.StudyObject
import org.stepik.core.SupportedLanguages


internal interface ProjectSetting {
    fun selectedStudyNode(studyObject: StudyObject)

    fun addListener(listener: ProjectSettingListener)

    fun removeListener(listener: ProjectSettingListener)

    fun selectedProgrammingLanguage(language: SupportedLanguages)
}
