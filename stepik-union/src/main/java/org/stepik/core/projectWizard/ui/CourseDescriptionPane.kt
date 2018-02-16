package org.stepik.core.projectWizard.ui

import com.intellij.ide.BrowserUtil
import com.intellij.ui.HyperlinkAdapter
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent


class CourseDescriptionPane : JTextPane() {
    init {
        isEditable = false
        contentType = "text/html"
        addHyperlinkListener(object : HyperlinkAdapter() {
            override fun hyperlinkActivated(e: HyperlinkEvent) {
                BrowserUtil.browse(e.url)
            }
        })
    }
}
