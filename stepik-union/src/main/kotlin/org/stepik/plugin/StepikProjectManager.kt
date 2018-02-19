package org.stepik.plugin

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.stepik.core.BaseProjectManager
import org.stepik.core.serialization.StudySerializationUtils
import org.stepik.plugin.serialization.SerializationUtils
import org.stepik.plugin.serialization.SerializationUtils.MAIN_ELEMENT

@State(name = "StepikStudySettings", storages = arrayOf(Storage("stepik_study_project.xml")))
class StepikProjectManager @JvmOverloads constructor(project: Project? = null) : BaseProjectManager(project) {
    override fun getVersion(state: Element): Int {
        return StudySerializationUtils.getVersion(state, MAIN_ELEMENT)
    }

    override fun getMainElement() = MAIN_ELEMENT

    override fun migrate(version: Int, state: Element): Element {
        var myState = state
        when (version) {
            1 -> {
                myState = SerializationUtils.convertToSecondVersion(myState)
                myState = SerializationUtils.convertToThirdVersion(myState)
                myState = SerializationUtils.convertToFourthVersion(myState)
            }
            2 -> {
                myState = SerializationUtils.convertToThirdVersion(myState)
                myState = SerializationUtils.convertToFourthVersion(myState)
            }
            3 -> myState = SerializationUtils.convertToFourthVersion(myState)
        }//uncomment for future versions
        //case 4:
        //myState = StudySerializationUtils.convertToFifthVersion(myState);
        return myState
    }

    override fun getCurrentVersion() = 4
}
