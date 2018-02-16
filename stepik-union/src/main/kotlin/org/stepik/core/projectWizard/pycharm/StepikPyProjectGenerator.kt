package org.stepik.core.projectWizard.pycharm

import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.DefaultProjectFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.BooleanFunction
import com.jetbrains.python.newProject.PyNewProjectSettings
import com.jetbrains.python.newProject.PythonProjectGenerator
import com.jetbrains.python.remote.PyProjectSynchronizer
import org.jetbrains.annotations.Nls
import org.stepik.core.StepikProjectManager
import org.stepik.core.StudyProjectComponent
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.projectWizard.ProjectWizardUtils
import org.stepik.core.projectWizard.ProjectWizardUtils.createSubDirectories
import org.stepik.core.projectWizard.StepikProjectGenerator
import org.stepik.core.stepik.StepikAuthManager
import org.stepik.core.stepik.StepikAuthManagerListener
import org.stepik.core.stepik.StepikAuthState
import org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory
import java.awt.Container
import java.io.File
import javax.swing.Icon
import javax.swing.JPanel


internal class StepikPyProjectGenerator private constructor() :
        PythonProjectGenerator<PyNewProjectSettings>(true),
        StepikAuthManagerListener {
    private val generator: StepikProjectGenerator = StepikProjectGenerator
    private val wizardStep: PyCharmWizardStep = PyCharmWizardStep(this)
    private val project: Project = DefaultProjectFactory.getInstance().defaultProject
    private var locationField: TextFieldWithBrowseButton? = null
    private var locationSetting: Boolean = false
    private var keepLocation: Boolean = false

    private var location: String
        get() {
            val locationField = getLocationField() ?: return ""
            return locationField.text
        }
        set(location) {
            if (keepLocation) {
                return
            }

            val locationField = getLocationField() ?: return

            locationSetting = true
            locationField.text = location
            locationSetting = false
        }

    init {
        StepikAuthManager.addListener(this)
    }

    override fun getLogo(): Icon? = AllStepikIcons.stepikLogo

    @Nls
    override fun getName() = MODULE_NAME

    @Throws(ProcessCanceledException::class)
    override fun extendBasePanel(): JPanel? {
        locationField = null
        keepLocation = false
        wizardStep.updateStep()
        return wizardStep.component
    }

    private fun getLocationField(): TextFieldWithBrowseButton? {
        if (locationField == null) {
            val basePanel = wizardStep.component?.parent ?: return null
            try {
                val topPanel = basePanel.getComponent(0) as Container
                val locationComponent = topPanel.getComponent(0) as LabeledComponent<*>
                locationField = locationComponent.component as TextFieldWithBrowseButton
            } catch (e: ClassCastException) {
                logger.warn("Auto naming for a project don't work: ", e)
                return null
            } catch (e: ArrayIndexOutOfBoundsException) {
                logger.warn("Auto naming for a project don't work: ", e)
                return null
            }

        }

        return locationField
    }

    override fun locationChanged(newLocation: String) {
        keepLocation = keepLocation || !locationSetting
    }

    override fun fireStateChanged() {
        if (!keepLocation && getLocationField() != null) {
            val studyObject = wizardStep.selectedStudyObject
            val projectDirectory = File(location).parent
            val projectName = ProjectWizardUtils.getProjectDefaultName(projectDirectory, studyObject)
            location = projectDirectory + "/" + projectName
        }

        super.fireStateChanged()
    }

    override fun validate(s: String): ValidationResult {
        return wizardStep.check()
    }

    override fun beforeProjectGenerated(sdk: Sdk?): BooleanFunction<PythonProjectGenerator<*>>? {
        return BooleanFunction {
            val studyObject = wizardStep.selectedStudyObject
            if (studyObject.id == 0L) {
                return@BooleanFunction false
            }

            ProjectWizardUtils.enrollmentCourse(studyObject)

            this.generator.createCourseNodeUnderProgress(project, studyObject)
            return@BooleanFunction true
        }
    }

    public override fun configureProject(
            project: Project,
            baseDir: VirtualFile,
            settings: PyNewProjectSettings,
            module: Module,
            synchronizer: PyProjectSynchronizer?) {
        super.configureProject(project, baseDir, settings, module, synchronizer)
        ApplicationManager.getApplication()
                .runWriteAction { ModuleRootModificationUtil.setModuleSdk(module, settings.sdk) }
        createCourseFromGenerator(project)
        dispose()
    }

    private fun createCourseFromGenerator(project: Project) {
        generator.generateProject(project)

        FileUtil.createDirectory(File(project.basePath, EduNames.SANDBOX_DIR))

        val projectManager = StepikProjectManager.getInstance(project)
        if (projectManager == null) {
            logger.warn("failed to generate builders: StepikProjectManager is null")
            return
        }
        projectManager.defaultLang = generator.defaultLang
        val root = projectManager.projectRoot
        if (root == null) {
            logger.warn("failed to generate builders: Root is null")
            return
        }

        if (root is StepNode) {
            getOrCreateSrcDirectory(project, (root as StepNode?)!!, true)
        } else {
            createSubDirectories(project, generator.defaultLang, root, null)
            VirtualFileManager.getInstance().syncRefresh()
        }

        val application = ApplicationManager.getApplication()
        application.invokeLater {
            application.runWriteAction {
                StudyProjectComponent.getInstance(project)
                        .registerStudyToolWindow()
                StepikProjectManager.updateAdaptiveSelected(project)
            }

        }
    }

    override fun stateChanged(oldState: StepikAuthState, newState: StepikAuthState) {
        ApplicationManager.getApplication().invokeLater { this.fireStateChanged() }
    }

    private fun dispose() {
        wizardStep.dispose()
        StepikAuthManager.removeListener(this)
    }

    companion object {
        private val logger = Logger.getInstance(StepikPyProjectGenerator::class.java)
        private const val MODULE_NAME = "Stepik"
    }
}
