package org.stepik.alt.courseFormat

import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.Node

class AltTree(project: Project? = null, stepikApiClient: StepikApiClient? = null, data: StudyObject? = StudyObject()) :
        Node(project, stepikApiClient, data) {
    override val childClass: Class<out Node>
        get() = LessonNode::class.java

    override val childDataClass: Class<out StudyObject>
        get() = CompoundUnitLesson::class.java

    override val dataClass: Class<out StudyObject>
        get() = StudyObject::class.java

    override fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean {
        return true
    }

    override fun getCourseId(stepikApiClient: StepikApiClient): Long {
        return 0
    }

    override fun getChildDataList(stepikApiClient: StepikApiClient): List<StudyObject> {
        val objects = mutableListOf<CompoundUnitLesson>()
        try {
            val lessonsIds = children.map { it.id }
            if (lessonsIds.isNotEmpty()) {
                stepikApiClient.lessons()
                        .get()
                        .id(lessonsIds)
                        .execute()
                        .forEach { lesson ->
                            objects.add(CompoundUnitLesson(lesson = lesson))
                        }
            }
        } catch (logged: StepikClientException) {
            logger.warn("A AltL initialization don't is fully", logged)
        }

        return objects
    }
}