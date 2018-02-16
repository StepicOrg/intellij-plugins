package org.stepik.core.projectWizard.ui

import com.intellij.icons.AllIcons
import java.awt.event.ActionEvent
import javax.swing.JButton


class RefreshButton : JButton() {
    internal var target: CourseListBox? = null

    init {
        icon = AllIcons.Actions.Refresh
    }

    override fun fireActionPerformed(event: ActionEvent) {
        super.fireActionPerformed(event)
        target?.refresh()
    }
}
