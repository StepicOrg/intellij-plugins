package org.stepik.plugin.projectWizard.ui

import org.stepik.api.objects.StudyObject
import org.stepik.core.SupportedLanguages
import java.awt.event.ItemEvent
import javax.swing.JComboBox


class CourseListBox : JComboBox<StudyObject>() {
    private val courseListModel: CourseListModel = CourseListModel()
    internal var target: ProjectSetting? = null
    private var programmingLanguage = SupportedLanguages.INVALID

    init {
        model = courseListModel
        val courseEditor = CourseListBoxEditor()
        courseEditor.model = courseListModel
        courseEditor.owner = this
        setEditor(courseEditor)
    }

    internal fun refresh(programmingLanguage: SupportedLanguages) {
        this.programmingLanguage = programmingLanguage
        courseListModel.update(programmingLanguage)
    }

    internal fun refresh() {
        courseListModel.update(programmingLanguage)
    }

    override fun getSelectedItem(): StudyObject {
        return courseListModel.selectedItem
    }

    override fun fireItemStateChanged(e: ItemEvent) {
        super.fireItemStateChanged(e)

        if (e.stateChange == ItemEvent.SELECTED) {
            target?.selectedStudyNode(e.item as StudyObject)
        }
    }
}
