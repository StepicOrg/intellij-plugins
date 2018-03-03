package org.stepik.alt

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.stepik.alt.courseFormat.AltTree
import org.stepik.core.BaseProjectManager
import org.stepik.core.StudyPluginConfigurator
import org.stepik.core.projectView.ProjectTreeMode.LESSON
import org.stepik.core.serialization.SerializationUtils
import org.stepik.core.serialization.StudySerializationUtils

@State(name = "AltSettings", storages = [(Storage("alt_project.xml"))])
class AltProjectManager @JvmOverloads constructor(project: Project? = null) : BaseProjectManager(project) {

    override val projectTreeMode = LESSON

    override fun getConfiguratorEPName(): ExtensionPointName<StudyPluginConfigurator> {
        return AltProjectConfigurator.EP_NAME
    }

    init {
        version = getCurrentVersion()
        SerializationUtils.xStream.alias("AltTree", AltTree::class.java)
    }

    override fun getVersion(state: Element): Int {
        return StudySerializationUtils.getVersion(state, javaClass.simpleName)
    }

    override fun getCurrentVersion() = 1
}
