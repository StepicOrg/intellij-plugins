package org.stepik.core.courseFormat

import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.StudyObject
import org.stepik.core.common.Loggable

interface StudyNode : Loggable {

    var project: Project?

    val id: Long

    val name: String

    val position: Int

    val directory: String

    val path: String

    var status: StudyStatus

    var parent: StudyNode?

    val isLeaf: Boolean

    val children: List<Node>

    var data: StudyObject

    var wasDeleted: Boolean

    fun getPrevChild(current: StudyNode?): StudyNode?

    fun getNextChild(current: StudyNode?): StudyNode?

    fun getCourseId(stepikApiClient: StepikApiClient): Long

    fun getChildById(id: Long): StudyNode?

    fun getChildByClassAndId(clazz: Class<out StudyNode>, id: Long): StudyNode?

    fun getChildByPosition(position: Int): StudyNode?

    fun init(project: Project?, stepikApiClient: StepikApiClient?, parent: StudyNode?)

    fun init(project: Project?, stepikApiClient: StepikApiClient?) {
        init(project, stepikApiClient, parent)
    }

    fun reloadData(project: Project, stepikApiClient: StepikApiClient)

    fun resetStatus()

    fun passed()
}
