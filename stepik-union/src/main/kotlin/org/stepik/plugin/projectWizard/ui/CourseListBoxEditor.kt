package org.stepik.plugin.projectWizard.ui

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.plaf.basic.BasicComboBoxEditor


internal class CourseListBoxEditor : BasicComboBoxEditor() {
    var model: CourseListModel? = null
    var owner: CourseListBox? = null

    init {
        editor.addKeyListener(OwnerKeyListener())
    }

    private inner class OwnerKeyListener : KeyAdapter() {
        override fun keyPressed(e: KeyEvent?) {
            if (e!!.keyCode == KeyEvent.VK_ENTER) {
                val text = editor.text
                model!!.setSelectedItem(text)
                val caretPos = text.length
                editor.select(caretPos, caretPos)
                owner!!.hidePopup()
                e.consume()
            }
        }

        override fun keyReleased(e: KeyEvent?) {
            val keyChar = e!!.keyChar
            if (keyChar.toInt() < KeyEvent.VK_SPACE || keyChar.toInt() == KeyEvent.VK_DELETE) {
                return
            }

            val inputtedText: String
            val caretPos = editor.selectionStart
            inputtedText = if (editor.selectionStart != editor.selectionEnd) {
                editor.text.substring(0, caretPos)
            } else {
                val text = editor.text
                if (editor.selectionEnd != text.length) {
                    return
                }
                text
            }

            val finalInputtedText = inputtedText.toLowerCase()

            val candidate = model!!.getCourses()
                    .filter { it.title.toLowerCase().startsWith(finalInputtedText) }
                    .sortedBy { it.title.length }
                    .firstOrNull()

            if (candidate != null) {
                model!!.selectedItem = candidate
                val newText = candidate.title
                editor.apply {
                    text = newText
                    selectionStart = caretPos
                    selectionEnd = newText.length
                }
            }
        }
    }
}
