package org.stepik.alt

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.stepik.core.BaseProjectManager
import org.stepik.core.serialization.StudySerializationUtils

@State(name = "AltSettings", storages = arrayOf(Storage("alt_project.xml")))
class AltProjectManager @JvmOverloads constructor(project: Project? = null) : BaseProjectManager(project) {

    init {
        version = getCurrentVersion()
    }

    override fun getVersion(state: Element): Int {
        return StudySerializationUtils.getVersion(state, javaClass.simpleName)
    }

    override fun getCurrentVersion() = 1
}
