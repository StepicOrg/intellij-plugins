package org.stepik.plugin.projectWizard.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import org.stepik.api.objects.StudyObject
import org.stepik.core.SupportedLanguages
import org.stepik.core.auth.StepikAuthManager
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.auth.StepikAuthManagerListener
import org.stepik.core.auth.StepikAuthState
import org.stepik.core.auth.StepikAuthState.AUTH
import org.stepik.core.auth.StepikAuthState.NOT_AUTH
import org.stepik.core.common.Loggable
import org.stepik.core.utils.EMPTY_STUDY_OBJECT
import org.stepik.core.utils.StepikUrlUtils.getCourseDescription
import java.awt.event.HierarchyEvent
import java.awt.event.HierarchyListener
import java.util.*
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

class ProjectSettingsPanel(visibleLangBox: Boolean) : ProjectSetting, HierarchyListener, StepikAuthManagerListener,
        Loggable {
    
    private val listeners = ArrayList<ProjectSettingListener>()
    private var mainPanel: JPanel? = null
    private var nameLabel: JLabel? = null
    private var userName: JLabel? = null
    private var langLabel: JLabel? = null
    private var langComboBox: LanguageComboBox? = null
    private var courseLabel: JLabel? = null
    private var courseListComboBox: CourseListBox? = null
    private var refreshListButton: RefreshButton? = null
    private var courseListDescription: CourseDescriptionPane? = null
    private var scrollPane: JScrollPane? = null
    private var loginButton: JButton? = null
    var selectedStudyObject = EMPTY_STUDY_OBJECT
        private set
    
    var language: SupportedLanguages
        get() = langComboBox!!.selectedItem
        set(language) {
            langComboBox!!.selectedItem = language
        }
    
    init {
        refreshListButton!!.target = courseListComboBox!!
        courseListComboBox!!.target = this
        langComboBox!!.target = this
        langComboBox!!.isVisible = visibleLangBox
        langLabel!!.isVisible = visibleLangBox
        mainPanel!!.addHierarchyListener(this)
        loginButton!!.addActionListener { StepikAuthManager.relogin() }
        StepikAuthManager.addListener(this)
    }
    
    fun getComponent() = mainPanel
    
    private fun setUsername() {
        val username = StepikAuthManager.currentUserFullName
        userName!!.text = username
    }
    
    fun updateStep() {
        logger.info("Start updating settings")
        courseListComboBox!!.refresh(langComboBox!!.selectedItem)
        setUsername()
        loginButton!!.text = if (isAuthenticated) "Change user" else "Login"
        logger.info("Updating settings is done")
    }
    
    fun validate(): Boolean {
        val valid = selectedStudyObject.id != 0L
        logger.info("Validation is $valid")
        return valid
    }
    
    override fun selectedStudyNode(studyObject: StudyObject) {
        selectedStudyObject = studyObject
        val description = getCourseDescription(studyObject)
        courseListDescription!!.text = description
        // Scroll to top
        courseListDescription!!.selectionStart = 0
        courseListDescription!!.selectionEnd = 0
        logger.info("Has selected the course: $studyObject")
        notifyListeners()
    }
    
    private fun notifyListeners() {
        listeners.forEach { it.changed() }
    }
    
    override fun addListener(listener: ProjectSettingListener) {
        listeners.add(listener)
    }
    
    override fun removeListener(listener: ProjectSettingListener) {
        listeners.remove(listener)
    }
    
    override fun selectedProgrammingLanguage(language: SupportedLanguages) {
        courseListComboBox!!.refresh(language)
    }
    
    override fun hierarchyChanged(e: HierarchyEvent) {
        notifyListeners()
    }
    
    override fun stateChanged(oldState: StepikAuthState, newState: StepikAuthState) {
        if (newState == NOT_AUTH || newState == AUTH) {
            ApplicationManager.getApplication()
                    .invokeLater({ this.updateStep() }, ModalityState.stateForComponent(mainPanel!!))
        }
    }
    
    fun dispose() {
        StepikAuthManager.removeListener(this)
    }
}
