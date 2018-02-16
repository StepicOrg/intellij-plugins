package org.stepik.core

import com.google.gson.internal.LinkedTreeMap
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleWithNameAlreadyExists
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFileManager
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.XStreamException
import com.thoughtworks.xstream.annotations.XStreamOmitField
import com.thoughtworks.xstream.io.xml.DomDriver
import org.jdom.Element
import org.jdom.JDOMException
import org.jdom.input.DOMBuilder
import org.jdom.output.XMLOutputter
import org.stepik.api.objects.courses.Course
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.sections.Section
import org.stepik.api.objects.steps.Limit
import org.stepik.api.objects.steps.Sample
import org.stepik.api.objects.steps.Step
import org.stepik.api.objects.steps.VideoUrl
import org.stepik.api.objects.users.User
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.CourseNode
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.SectionNode
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.projectWizard.idea.SandboxModuleBuilder
import org.stepik.core.serialization.SampleConverter
import org.stepik.core.serialization.StudySerializationUtils
import org.stepik.core.serialization.StudyUnrecognizedFormatException
import org.stepik.core.serialization.SupportedLanguagesConverter
import org.stepik.core.stepik.StepikAuthManager
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated
import org.stepik.core.stepik.StepikAuthManagerListener
import org.stepik.core.stepik.StepikAuthState
import org.stepik.core.stepik.StepikAuthState.AUTH
import org.stepik.core.stepik.StepikAuthState.NOT_AUTH
import org.stepik.core.stepik.StepikAuthState.SHOW_DIALOG
import org.stepik.core.utils.ProjectFilesUtils
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.Executors
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

@State(name = "StepikStudySettings", storages = arrayOf(Storage("stepik_study_project.xml")))
class StepikProjectManager @JvmOverloads constructor(@field:XStreamOmitField
                                                     val project: Project? = null) :
        PersistentStateComponent<Element>, DumbAware, StepikAuthManagerListener, Disposable {
    @XStreamOmitField
    private val executor = Executors.newSingleThreadExecutor()
    private var root: StudyNode<*, *>? = null
    private var selected: StudyNode<*, *>? = null
    var showHint = false
    private var createdBy: Long = 0
    var defaultLang: SupportedLanguages? = SupportedLanguages.INVALID
    var version = CURRENT_VERSION
        private set
    internal var uuid: String? = null

    private val isAdaptive: Boolean
        get() {
            if (root == null) {
                return false
            }
            val data = root!!.data
            return data != null && data.isAdaptive
        }

    val projectRoot: StudyNode<*, *>?
        get() = root

    init {
        StepikAuthManager.addListener(this)
        if (project != null) {
            Disposer.register(project, this)
        }
    }

    private fun updateAdaptiveSelected() {
        if (isAdaptive && root != null) {
            val recommendation = StudyUtils.getRecommendation(root!!)
            if (selected == null || recommendation != null && selected!!.parent !== recommendation.parent) {
                ApplicationManager.getApplication().invokeAndWait {
                    for (file in FileEditorManager.getInstance(project!!).openFiles) {
                        FileEditorManager.getInstance(project).closeFile(file)
                    }
                }
                setSelected(recommendation)
            }
        }
    }

    private fun getSelected(): StudyNode<*, *>? {
        return selected
    }

    @JvmOverloads
    fun setSelected(selected: StudyNode<*, *>?, force: Boolean = false) {
        this.selected = selected
        if (project != null) {
            val toolWindow = StudyUtils.getStudyToolWindow(project)
            if (toolWindow != null) {
                ApplicationManager.getApplication().invokeLater {
                    toolWindow.setStepNode(selected, force)
                    if (selected != null && !project.isDisposed) {
                        ProjectView.getInstance(project).refresh()
                        val file = project.baseDir.findFileByRelativePath(selected.path)
                        ProjectView.getInstance(project).select(null, file, true)
                    }
                }
            }
        }
    }

    private fun updateSelection() {
        setSelected(selected, true)
    }

    fun setRootNode(root: StudyNode<*, *>?) {
        this.root = root
    }

    override fun getState(): Element? {
        if (projectRoot == null) {
            return null
        }
        try {
            ByteArrayOutputStream().use { out ->
                xStream.toXML(this, OutputStreamWriter(out, UTF_8))
                val el = toElement(out)
                logger.info("Getting the StepikProjectManager state")

                return el
            }
        } catch (e: ParserConfigurationException) {
            logger.warn("Failed getting the StepikProjectManager state", e)
        } catch (e: IOException) {
            logger.warn("Failed getting the StepikProjectManager state", e)
        } catch (e: SAXException) {
            logger.warn("Failed getting the StepikProjectManager state", e)
        }

        return null
    }

    override fun loadState(state: Element) {
        var myState = state
        try {
            logger.info("Start load the StepikProjectManager state")
            val version = StudySerializationUtils.getVersion(myState)

            when (version) {
                1 -> {
                    myState = StudySerializationUtils.convertToSecondVersion(myState)
                    myState = StudySerializationUtils.convertToThirdVersion(myState)
                    myState = StudySerializationUtils.convertToFourthVersion(myState)
                }
                2 -> {
                    myState = StudySerializationUtils.convertToThirdVersion(myState)
                    myState = StudySerializationUtils.convertToFourthVersion(myState)
                }
                3 -> myState = StudySerializationUtils.convertToFourthVersion(myState)
            }//uncomment for future versions
            //case 4:
            //myState = StudySerializationUtils.convertToFifthVersion(myState);

            val xml = elementToXml(myState)
            xStream.fromXML(xml, this)

            this.version = CURRENT_VERSION
            refreshCourse()
            updateSelection()
            logger.info("The StepikProjectManager state loaded")
        } catch (e: XStreamException) {
            logger.warn("Failed deserialization StepikProjectManager \n${e.message}\n$project")
        } catch (e: StudyUnrecognizedFormatException) {
            logger.warn("Failed deserialization StepikProjectManager \n${e.message}\n$project")
        }

    }

    private fun refreshCourse() {
        if (project == null || root == null) {
            return
        }

        root!!.setProject(project)

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
                        setSelected(selected, false)
                    }
                }

                private fun repairSandbox() {
                    val projectDir = project!!.baseDir
                    if (projectDir != null && projectDir.findChild(EduNames.SANDBOX_DIR) == null) {
                        val application = ApplicationManager.getApplication()

                        val model = application.runReadAction(Computable {
                            ModuleManager.getInstance(project!!)
                                    .modifiableModel
                        })

                        application.invokeLater {
                            application.runWriteAction {
                                try {
                                    SandboxModuleBuilder(projectDir.path).createModule(model)
                                    model.commit()
                                } catch (e: IOException) {
                                    logger.warn("Failed repair Sandbox", e)
                                } catch (e: ConfigurationException) {
                                    logger.warn("Failed repair Sandbox", e)
                                } catch (e: JDOMException) {
                                    logger.warn("Failed repair Sandbox", e)
                                } catch (e: ModuleWithNameAlreadyExists) {
                                    logger.warn("Failed repair Sandbox", e)
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun repairProjectFiles(node: StudyNode<*, *>) {
        if (project != null) {
            if (node is StepNode) {
                ApplicationManager.getApplication().invokeAndWait {
                    if (project.isDisposed) {
                        return@invokeAndWait
                    }
                    ProjectFilesUtils.getOrCreateSrcDirectory(project, node, false)
                }
            }
            node.children.forEach { this.repairProjectFiles(it) }
        }
    }

    fun setCreatedBy(createdBy: Long) {
        this.createdBy = createdBy
    }

    fun getUuid(): String {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
        }
        return uuid!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as StepikProjectManager?

        if (showHint != that!!.showHint) return false
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
                    refreshCourse()
                }
            }

            if (oldState === SHOW_DIALOG && newState === NOT_AUTH) {
                setSelected(selected, false)
            } else {
                updateSelection()
            }
        }
    }

    override fun dispose() {
        StepikAuthManager.removeListener(this)
        executor.shutdown()
    }

    companion object {
        private const val CURRENT_VERSION = 4
        private val logger = Logger.getInstance(StepikProjectManager::class.java)
        @XStreamOmitField
        val xStream: XStream = {
            XStream(DomDriver()).apply {
                alias("StepikProjectManager", StepikProjectManager::class.java)
                alias("CourseNode", CourseNode::class.java)
                alias("SectionNode", SectionNode::class.java)
                alias("LessonNode", LessonNode::class.java)
                alias("StepNode", StepNode::class.java)
                alias("Limit", Limit::class.java)
                alias("SupportedLanguages", SupportedLanguages::class.java)
                alias("VideoUrl", VideoUrl::class.java)
                alias("LinkedTreeMap", LinkedTreeMap::class.java)
                alias("Sample", Sample::class.java)
                alias("Course", Course::class.java)
                alias("Section", Section::class.java)
                alias("CompoundUnitLesson", CompoundUnitLesson::class.java)
                alias("Step", Step::class.java)
                alias("User", User::class.java)
                autodetectAnnotations(true)
                classLoader = StepikProjectManager::class.java.classLoader
                registerConverter(SupportedLanguagesConverter())
                registerConverter(SampleConverter())
                ignoreUnknownElements()
                setMode(XStream.ID_REFERENCES)
            }
        }.invoke()
        @XStreamOmitField
        private var outputter: XMLOutputter? = null
        @XStreamOmitField
        private var factory: DocumentBuilderFactory? = null
        @XStreamOmitField
        private var builder: DocumentBuilder? = null
        @XStreamOmitField
        private var domBuilder: DOMBuilder? = null

        fun getInstance(project: Project): StepikProjectManager? {
            return ServiceManager.getService(project, StepikProjectManager::class.java)
        }

        fun getProjectRoot(project: Project): StudyNode<*, *>? {
            if (project.isDisposed) {
                return null
            }
            val instance = getInstance(project) ?: return null

            return instance.projectRoot
        }

        fun isStepikProject(project: Project?): Boolean {
            if (project == null) {
                return false
            }
            val instance = getInstance(project)
            return instance?.projectRoot != null
        }

        private fun elementToXml(state: Element): String {
            if (outputter == null) {
                outputter = XMLOutputter()
            }
            return outputter!!.outputString(state.getChild(StudySerializationUtils.MAIN_ELEMENT))
        }

        @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
        private fun toElement(out: ByteArrayOutputStream): Element {
            if (factory == null) {
                factory = DocumentBuilderFactory.newInstance()
                factory!!.isValidating = false
                builder = factory!!.newDocumentBuilder()
                domBuilder = DOMBuilder()
            }
            ByteArrayInputStream(out.toByteArray()).use { `in` ->
                val doc = builder!!.parse(`in`)
                val document = domBuilder!!.build(doc)

                val root = document.rootElement
                document.removeContent(root)

                val element = Element("element")
                element.addContent(root)
                return element
            }
        }

        @JvmOverloads
        fun setSelected(project: Project, studyNode: StudyNode<*, *>?, force: Boolean = false) {
            val instance = getInstance(project)
            instance?.setSelected(studyNode, force)
        }

        fun updateSelection(project: Project) {
            val instance = getInstance(project)
            instance?.updateSelection()
        }

        fun getSelected(project: Project?): StudyNode<*, *>? {
            if (project == null) {
                return null
            }
            val instance = getInstance(project)
            return instance?.getSelected()

        }

        fun isAdaptive(project: Project): Boolean {
            val instance = getInstance(project)
            return instance != null && instance.isAdaptive
        }

        fun updateAdaptiveSelected(project: Project?) {
            if (project == null) {
                return
            }
            val instance = getInstance(project)
            instance?.updateAdaptiveSelected()
        }
    }
}
