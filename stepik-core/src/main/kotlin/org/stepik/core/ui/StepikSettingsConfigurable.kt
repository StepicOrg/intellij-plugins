package org.stepik.core.ui

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsConfigurableProvider
import javax.swing.JComponent

class StepikSettingsConfigurable : SearchableConfigurable, VcsConfigurableProvider {
    private var settingsPane: StepikSettingsPanel? = null

    override fun getDisplayName() = "Stepik"

    override fun getHelpTopic() = "settings.stepik"

    override fun createComponent(): JComponent {
        if (settingsPane == null) {
            settingsPane = StepikSettingsPanel()
        }
        return settingsPane!!.panel!!
    }

    override fun isModified() = settingsPane?.isModified ?: false

    @Throws(ConfigurationException::class)
    override fun apply() {
        settingsPane?.apply()
    }

    override fun reset() {
        settingsPane?.reset()
    }

    override fun disposeUIResources() {
        settingsPane?.dispose()
        settingsPane = null
    }

    override fun getId() = helpTopic

    override fun enableSearch(option: String?): Runnable? = null

    override fun getConfigurable(project: Project) = this
}
