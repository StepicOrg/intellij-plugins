package org.stepik.plugin.projectWizard.ui

import org.stepik.core.SupportedLanguages
import java.awt.event.ItemEvent
import javax.swing.JComboBox


class LanguageComboBox : JComboBox<SupportedLanguages>() {
    internal var target: ProjectSetting? = null

    init {
        SupportedLanguages.values()
                .filter { it !== SupportedLanguages.INVALID }
                .forEach { this.addItem(it) }
        selectedItem = SupportedLanguages.JAVA8
    }

    override fun getSelectedItem(): SupportedLanguages {
        return super.getSelectedItem() as? SupportedLanguages ?: return SupportedLanguages.INVALID
    }

    override fun fireItemStateChanged(e: ItemEvent) {
        super.fireItemStateChanged(e)

        if (e.stateChange == ItemEvent.SELECTED) {
            target?.selectedProgrammingLanguage(e.item as SupportedLanguages)
        }
    }
}
