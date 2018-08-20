package org.hyperskill

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.stepik.core.BaseProjectManager
import org.stepik.core.StudyPluginConfigurator
import org.stepik.core.projectView.ProjectTreeMode.LESSON
import org.stepik.core.serialization.SerializationUtils
import org.stepik.core.serialization.StudySerializationUtils
import org.hyperskill.courseFormat.HyperskillTree

@State(name = "HyperskillSettings", storages = [(Storage("hyperskill_project.xml"))])
class HyperskillProjectManager @JvmOverloads constructor(project: Project? = null) : BaseProjectManager(project) {
    
    override val projectTreeMode = LESSON
    
    override fun getConfiguratorEPName(): ExtensionPointName<StudyPluginConfigurator> {
        return HyperskillProjectConfigurator.EP_NAME
    }
    
    init {
        version = getCurrentVersion()
        SerializationUtils.xStream.alias("HyperskillTree", HyperskillTree::class.java)
    }
    
    override fun getVersion(state: Element): Int {
        return StudySerializationUtils.getVersion(state, javaClass.simpleName)
    }
    
    override fun getCurrentVersion() = 1
}
