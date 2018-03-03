package org.stepik.core

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFileManager
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.jdom.Element
import org.stepik.core.auth.StepikAuthManager
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.auth.StepikAuthManagerListener
import org.stepik.core.auth.StepikAuthState
import org.stepik.core.auth.StepikAuthState.AUTH
import org.stepik.core.auth.StepikAuthState.NOT_AUTH
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.serialization.SerializationUtils.elementToXml
import org.stepik.core.serialization.SerializationUtils.toElement
import org.stepik.core.serialization.SerializationUtils.xStream
import org.stepik.core.utils.getOrCreateSrcDirectory
import org.stepik.core.utils.runWriteActionLater
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.Executors

abstract class BaseProjectManager constructor(@field:XStreamOmitField val project: Project? = null) :
        PersistentStateComponent<Element>, DumbAware, StepikAuthManagerListener, Disposable, Loggable, ProjectManager {
    @XStreamOmitField
    private val executor = Executors.newSingleThreadExecutor()
    private var root: StudyNode? = null
    override var selected: StudyNode? = null
        set(value) {
            field = value
            updateToolWindow()
        }
    override var showHint = false
    override var createdBy: Long = 0
    override var defaultLang: SupportedLanguages? = SupportedLanguages.INVALID
    override var version = 0

    open fun getCurrentVersion(): Int = 1

    private var uuid: String? = null

    override val isAdaptive: Boolean
        get() {
            if (root == null) {
                return false
            }
            val data = root!!.data
            return data.isAdaptive
        }

    override var projectRoot: StudyNode?
        get() = root
        set(value) {
            root = value
        }

    init {
        StepikAuthManager.addListener(this)
        if (project != null) {
            Disposer.register(project, this)
        }
        xStream.alias(javaClass.simpleName, javaClass)
    }

    override fun updateAdaptiveSelected() {
        if (isAdaptive && root != null) {
            val recommendation = StudyUtils.getRecommendation(root!!)
            if (selected == null || recommendation != null && selected!!.parent !== recommendation.parent) {
                getApplication().invokeAndWait {
                    for (file in FileEditorManager.getInstance(project!!).openFiles) {
                        FileEditorManager.getInstance(project).closeFile(file)
                    }
                }
                selected = recommendation
            }
        }
    }

    private fun updateToolWindow(force: Boolean = false) {
        if (project != null) {
            val toolWindow = StudyUtils.getStudyToolWindow(project) ?: return
            getApplication().invokeLater {
                val selected = selected
                toolWindow.setStepNode(selected, force)
                if (selected != null && !project.isDisposed) {
                    ProjectView.getInstance(project).refresh()
                    val file = project.baseDir.findFileByRelativePath(selected.path)
                    ProjectView.getInstance(project).select(null, file, true)
                }
            }
        }
    }

    override fun updateSelection() {
        updateToolWindow(true)
    }

    override fun getState(): Element? {
        if (projectRoot == null) {
            return null
        }
        try {
            ByteArrayOutputStream().use { out ->
                xStream.toXML(this, OutputStreamWriter(out, UTF_8))
                val el = toElement(out)
                logger.info("Getting the ${javaClass.simpleName} state")

                return el
            }
        } catch (e: Exception) {
            logger.warn("Failed getting the ${javaClass.simpleName} state", e)
        }

        return null
    }

    open fun migrate(version: Int, state: Element): Element {
        return state
    }

    abstract fun getVersion(state: Element): Int

    override fun loadState(state: Element) {
        var myState = state
        try {
            logger.info("Start load the ${javaClass.simpleName} state")
            val version = getVersion(myState)

            myState = migrate(version, myState)

            val xml = elementToXml(myState, javaClass.simpleName)
            xStream.fromXML(xml, this)

            this.version = getCurrentVersion()
            refreshProjectFiles()
            updateSelection()
            logger.info("The ${javaClass.simpleName} state loaded")
        } catch (e: Exception) {
            logger.warn("Failed deserialization ${javaClass.simpleName} \n${e.message}\n$project")
        }
    }

    override fun refreshProjectFiles() {
        if (project == null || root == null) {
            return
        }

        root!!.project = project

        executor.execute {
            val stepikApiClient = authAndGetStepikApiClient()
            if (isAuthenticated) {
                root!!.reloadData(project, stepikApiClient)
            }
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Synchronize Project") {
                override fun run(indicator: ProgressIndicator) {
                    if (project!!.isDisposed) {
                        return
                    }

                    repairProjectFiles(root!!)
                    repairSandbox()

                    ApplicationManager.getApplication().invokeLater {
                        VirtualFileManager.getInstance().syncRefresh()
                        updateToolWindow()
                    }
                }

                private fun repairSandbox() {
                    val projectDir = project!!.baseDir
                    if (projectDir != null && projectDir.findChild(EduNames.SANDBOX_DIR) == null) {
                        val application = ApplicationManager.getApplication()

                        val model = application.runReadAction(Computable {
                            ModuleManager.getInstance(project!!).modifiableModel
                        })

                        application.runWriteActionLater {
                            try {
                                val moduleBuilder = getConfigurator(project)
                                        ?.getSandboxModuleBuilder(projectDir.path)
                                val module = moduleBuilder?.createModule(model)
                                if (module == null) {
                                    logger.warn("Failed repair Sandbox")
                                    return@runWriteActionLater
                                }
                                model.commit()
                            } catch (e: Exception) {
                                logger.warn("Failed repair Sandbox", e)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun repairProjectFiles(node: StudyNode) {
        if (project != null) {
            if (node is StepNode) {
                ApplicationManager.getApplication().invokeAndWait {
                    if (project.isDisposed) {
                        return@invokeAndWait
                    }
                    getOrCreateSrcDirectory(project, node, false)
                }
            }
            node.children.forEach { this.repairProjectFiles(it) }
        }
    }

    override fun getUuid(): String {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
        }
        return uuid!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as BaseProjectManager

        if (showHint != that.showHint) return false
        if (createdBy != that.createdBy) return false
        if (version != that.version) return false
        if (if (project != null) project != that.project else that.project != null) return false
        if (if (root != null) root != that.root else that.root != null) return false

        if (defaultLang !== that.defaultLang) return false
        return if (uuid != null) uuid == that.uuid else that.uuid == null
    }

    override fun hashCode(): Int {
        var result = project?.hashCode() ?: 0
        result = 31 * result + if (root != null) root!!.hashCode() else 0
        result = 31 * result + if (showHint) 1 else 0
        result = 31 * result + (createdBy xor createdBy.ushr(32)).toInt()
        result = 31 * result + if (defaultLang != null) defaultLang!!.hashCode() else 0
        result = 31 * result + version
        result = 31 * result + if (uuid != null) uuid!!.hashCode() else 0
        return result
    }

    override fun stateChanged(oldState: StepikAuthState, newState: StepikAuthState) {
        if (newState === NOT_AUTH || newState === AUTH) {
            if (root != null) {
                root!!.resetStatus()
                if (newState === AUTH) {
                    refreshProjectFiles()
                }
            }

            updateSelection()
        }
    }

    override fun dispose() {
        StepikAuthManager.removeListener(this)
        executor.shutdown()
    }


    override fun getConfigurator(project: Project): StudyPluginConfigurator? {
        val extensions = getConfiguratorEPName().extensions
        return extensions.firstOrNull { it.accept(project) }
    }

    abstract fun getConfiguratorEPName(): ExtensionPointName<StudyPluginConfigurator>
}
