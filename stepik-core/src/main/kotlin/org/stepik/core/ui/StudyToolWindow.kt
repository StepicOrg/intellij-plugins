package org.stepik.core.ui

import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.JBCardLayout
import com.intellij.ui.OnePixelSplitter
import com.intellij.util.ui.JBUI
import org.stepik.api.exceptions.StepikClientException
import org.stepik.core.ProjectManager
import org.stepik.core.StudyBasePluginConfigurator
import org.stepik.core.StudyUtils
import org.stepik.core.StudyUtils.isStepikProject
import org.stepik.core.SupportedLanguages
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StepType
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.courseFormat.stepHelpers.Actions.GET_FIRST_ATTEMPT
import org.stepik.core.courseFormat.stepHelpers.ChoiceQuizHelper
import org.stepik.core.courseFormat.stepHelpers.CodeQuizHelper
import org.stepik.core.courseFormat.stepHelpers.DatasetQuizHelper
import org.stepik.core.courseFormat.stepHelpers.FillBlanksQuizHelper
import org.stepik.core.courseFormat.stepHelpers.FreeAnswerQuizHelper
import org.stepik.core.courseFormat.stepHelpers.MatchingQuizHelper
import org.stepik.core.courseFormat.stepHelpers.MathQuizHelper
import org.stepik.core.courseFormat.stepHelpers.NumberQuizHelper
import org.stepik.core.courseFormat.stepHelpers.SortingQuizHelper
import org.stepik.core.courseFormat.stepHelpers.StepHelper
import org.stepik.core.courseFormat.stepHelpers.StringQuizHelper
import org.stepik.core.courseFormat.stepHelpers.TableQuizHelper
import org.stepik.core.courseFormat.stepHelpers.TextTheoryHelper
import org.stepik.core.courseFormat.stepHelpers.VideoTheoryHelper
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated
import org.stepik.core.utils.ProgrammingLanguageUtils
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Panel
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import java.util.concurrent.Executors
import javax.swing.BoxLayout
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities

class StudyToolWindow internal constructor() :
        SimpleToolWindowPanel(true, true), DataProvider, Disposable, ActionListener {

    private val languageBox: JComboBox<SupportedLanguages>
    private val cardLayout: JBCardLayout
    private val contentPanel: JPanel
    private val splitPane: OnePixelSplitter
    private val layout: CardLayout
    private val rightPanel: Panel
    private var project: Project? = null
    private var stepNode: StepNode? = null
    private var browserWindow: StudyBrowserWindow? = null

    private fun createStepInfoPanel(project: Project): JComponent {
        val browserWindow = StudyBrowserWindow(project)
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
        panel.add(browserWindow.panel)
        this.browserWindow = browserWindow
        return panel
    }

    private fun setEmptyText() {
        val context = mapOf("content" to EMPTY_STEP_TEXT)
        browserWindow?.loadContent("template", context)
        if (!isAuthenticated) {
            browserWindow?.callFunction("showLogin")
        }
    }

    private fun createToolbarPanel(group: ActionGroup): JPanel {
        val actionToolBar = ActionManager.getInstance().createActionToolbar("Study", group, true)
        val toolBar = JBUI.Panels.simplePanel(actionToolBar.component)
        toolBar.addToRight(rightPanel)
        return toolBar
    }

    fun init(project: Project) {
        this.project = project
        val group = getActionGroup(project)
        setActionToolbar(group)

        val panel = JPanel(BorderLayout())

        val stepInfoPanel = createStepInfoPanel(project)
        panel.add(stepInfoPanel, BorderLayout.CENTER)

        contentPanel.add(STEP_INFO_ID, panel)
        splitPane.firstComponent = contentPanel
        addAdditionalPanels(project)
        cardLayout.show(contentPanel, STEP_INFO_ID)

        setContent(splitPane)

        val configurator = StudyUtils.getConfigurator(project)
        if (configurator != null) {
            val listener = configurator.getFileEditorManagerListener(project)
            project.messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener)
        }
        val projectManager = getService(project, ProjectManager::class.java)
        val stepNode = projectManager?.selected
        setStepNode(stepNode)
    }

    private fun setActionToolbar(group: DefaultActionGroup) {
        val toolbarPanel = createToolbarPanel(group)
        setToolbar(toolbarPanel)
    }

    private fun addAdditionalPanels(project: Project) {
        val configurator = StudyUtils.getConfigurator(project)
        if (configurator != null) {
            val panels = configurator.getAdditionalPanels(project)
            for ((key, value) in panels) {
                contentPanel.add(key, value)
            }
        }
    }

    override fun dispose() {
        setStepNode(null)
    }

    private fun setStepNode(studyNode: StudyNode?) {
        setStepNode(studyNode, false)
    }

    fun setStepNode(studyNode: StudyNode?, force: Boolean) {
        browserWindow?.hideLoadAnimation()

        if (!force && stepNode === studyNode) {
            if (stepNode == null) {
                setEmptyText()
            }
            return
        }

        if (studyNode != null && studyNode !is StepNode) {
            setEmptyText()
            stepNode = null
            rightPanel.isVisible = false
            return
        }

        stepNode = studyNode as StepNode?

        executor.execute { setText(stepNode) }
    }

    private fun setText(stepNode: StepNode?) {
        if (stepNode == null) {
            setEmptyText()
            rightPanel.isVisible = false
            return
        }

        browserWindow?.showLoadAnimation()

        val stepType = stepNode.type
        if (stepType != StepType.CODE) {
            SwingUtilities.invokeLater { rightPanel.isVisible = false }
            stepNode.currentLang.runner.updateRunConfiguration(project!!, stepNode)
        }
        val isTheory = stepType in listOf(StepType.VIDEO, StepType.TEXT)
        postView(stepNode, isTheory)

        val stepHelper = when (stepType) {
            StepType.UNKNOWN -> StepHelper(project!!, stepNode)
            StepType.CODE -> {
                updateLanguageBox(stepNode)
                CodeQuizHelper(project!!, stepNode)
            }
            StepType.TEXT -> TextTheoryHelper(project!!, stepNode)
            StepType.VIDEO -> VideoTheoryHelper(project!!, stepNode)
            StepType.CHOICE -> ChoiceQuizHelper(project!!, stepNode)
            StepType.STRING -> StringQuizHelper(project!!, stepNode)
            StepType.SORTING -> SortingQuizHelper(project!!, stepNode)
            StepType.MATCHING -> MatchingQuizHelper(project!!, stepNode)
            StepType.NUMBER -> NumberQuizHelper(project!!, stepNode)
            StepType.DATASET -> DatasetQuizHelper(project!!, stepNode)
            StepType.TABLE -> TableQuizHelper(project!!, stepNode)
            StepType.FILL_BLANKS -> FillBlanksQuizHelper(project!!, stepNode)
            StepType.MATH -> MathQuizHelper(project!!, stepNode)
            StepType.FREE_ANSWER -> FreeAnswerQuizHelper(project!!, stepNode)
        }

        if (stepHelper.isAutoCreateAttempt && stepHelper.getAction() === GET_FIRST_ATTEMPT) {
            FormListener.getAttempt(project!!, stepNode)
        } else {
            val context = HashMap<String, Any>()
            context["stepNode"] = stepHelper
            context["darcula"] = LafManager.getInstance().currentLookAndFeel is DarculaLookAndFeelInfo
            browserWindow?.loadContent("quiz/${stepHelper.type}", context)
        }
    }

    private fun updateLanguageBox(stepNode: StepNode) {
        SwingUtilities.invokeLater {
            languageBox.removeAllItems()
            stepNode.supportedLanguages
                    .sortedBy { it.ordinal }
                    .forEach { languageBox.addItem(it) }
            layout.show(rightPanel, "language")
            val rightPanelVisible = languageBox.model.size != 0
            rightPanel.isVisible = rightPanelVisible
            languageBox.setSelectedItem(stepNode.currentLang)
        }
    }

    private fun postView(stepNode: StepNode, isTheory: Boolean) {
        executor.execute {
            val assignment = stepNode.assignment
            val stepId = stepNode.id

            if (assignment != null && assignment != 0L) {
                val stepikApiClient = authAndGetStepikApiClient()
                if (!isAuthenticated) {
                    return@execute
                }

                try {
                    stepikApiClient.views()
                            .post()
                            .assignment(assignment)
                            .step(stepId)
                            .execute()
                } catch (e: StepikClientException) {
                    logger.warn("Failed post view: stepId=$stepId; assignment=$assignment", e)
                }

            }

            if (isTheory) {
                stepNode.passed()
            }

            if (stepNode.project == null) {
                stepNode.project = project
            }
            if (!project!!.isDisposed) {
                ApplicationManager.getApplication().invokeLater { ProjectView.getInstance(project!!).refresh() }
            }
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        if (stepNode == null || languageBox.selectedItem == null) {
            return
        }

        val targetNode = stepNode

        executor.execute {
            val selectedLang = arrayOfNulls<SupportedLanguages>(1)
            ApplicationManager.getApplication().invokeAndWait {
                selectedLang[0] = languageBox.selectedItem as? SupportedLanguages
            }

            if (selectedLang[0] != null) {
                ProgrammingLanguageUtils.switchProgrammingLanguage(project!!, targetNode!!, selectedLang[0]!!)
                if (selectedLang[0] !== targetNode.currentLang) {
                    ApplicationManager.getApplication().invokeLater {
                        languageBox.setSelectedItem(targetNode.currentLang)
                    }
                }
            }
        }
    }

    companion object : Loggable {
        private const val STEP_INFO_ID = "stepInfo"
        private const val EMPTY_STEP_TEXT = "Please, open any step to see step description"
        private val executor = Executors.newSingleThreadExecutor()

        private fun getActionGroup(project: Project): DefaultActionGroup {
            val group = DefaultActionGroup()
            if (!isStepikProject(project)) {
                logger.warn("${project.name} is not Stepik-project")
                return group
            }
            val configurator = StudyUtils.getConfigurator(project)
            return if (configurator != null) {
                group.addAll(configurator.getActionGroup(project))
                group
            } else {
                logger.warn("No configurator is provided for plugin")
                StudyBasePluginConfigurator.defaultActionGroup
            }
        }
    }

    init {
        this.languageBox = ComboBox()
        this.cardLayout = JBCardLayout()
        this.contentPanel = JPanel(cardLayout)
        this.splitPane = OnePixelSplitter(true)
        this.layout = CardLayout()
        this.rightPanel = Panel(layout)
        languageBox.addActionListener(this)
        rightPanel.add("language", languageBox)
        rightPanel.isVisible = false
    }
}
