package org.stepik.plugin

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.stepik.core.BaseProjectManager
import org.stepik.plugin.serialization.StudySerializationUtils
import org.stepik.plugin.serialization.StudySerializationUtils.MAIN_ELEMENT

@State(name = "StepikStudySettings", storages = arrayOf(Storage("stepik_study_project.xml")))
class StepikProjectManager @JvmOverloads constructor(project: Project? = null) : BaseProjectManager(project) {
    override fun getVersion(state: Element): Int {
        return StudySerializationUtils.getVersion(state)
    }

    override fun getMainElement() = MAIN_ELEMENT

    override fun migrate(version: Int, state: Element): Element {
        var myState = state
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
        return myState
    }

    override fun getCurrentVersion() = 4
}
