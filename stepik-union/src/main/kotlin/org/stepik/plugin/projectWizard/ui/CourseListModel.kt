package org.stepik.plugin.projectWizard.ui

import org.stepik.api.objects.StudyObject
import org.stepik.core.SupportedLanguages
import org.stepik.core.utils.EMPTY_STUDY_OBJECT
import org.stepik.core.utils.StepikUrlUtils.getStudyObjectFromLink
import org.stepik.plugin.projectWizard.StepikProjectGenerator
import java.io.Serializable
import java.util.*
import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel
import javax.swing.SwingUtilities

internal class CourseListModel : AbstractListModel<StudyObject>(), ComboBoxModel<StudyObject>, Serializable {
    private val courses = ArrayList<StudyObject>()
    private var selectedItem: Any? = null
    
    override fun getSize(): Int {
        return courses.size
    }
    
    override fun getElementAt(index: Int): StudyObject {
        return courses.getOrElse(index) { EMPTY_STUDY_OBJECT }
    }
    
    fun update(programmingLanguage: SupportedLanguages) {
        StepikProjectGenerator.getCourses(programmingLanguage)
                .thenAccept { newCourseList ->
                    var selectedCourse = getSelectedItem()
                    courses.clear()
                    
                    if (!newCourseList.isEmpty()) {
                        courses.addAll(newCourseList)
                        if (selectedCourse === EMPTY_STUDY_OBJECT || !courses.contains(selectedCourse)) {
                            selectedCourse = courses[0]
                        }
                    } else {
                        courses.add(EMPTY_STUDY_OBJECT)
                    }
                    
                    val finalSelectedCourse = selectedCourse
                    SwingUtilities.invokeLater {
                        setSelectedItem(finalSelectedCourse)
                        fireIntervalAdded(this, 0, size - 1)
                    }
                }
    }
    
    override fun getSelectedItem(): StudyObject {
        return if (selectedItem !is StudyObject) {
            EMPTY_STUDY_OBJECT
        } else selectedItem as StudyObject
    }
    
    override fun setSelectedItem(anItem: Any?) {
        selectedItem = if (anItem != null && anItem !is StudyObject) {
            getCourse(anItem.toString())
        } else {
            anItem
        }
        fireContentsChanged(this, -1, -1)
    }
    
    private fun getCourse(link: String): Any {
        val finalLink = link.toLowerCase()
        
        return courses.firstOrNull { studyObject ->
            studyObject.title.toLowerCase() == finalLink
        } ?: getStudyObjectFromLink(link)
    }
    
    fun getCourses() = courses
}
