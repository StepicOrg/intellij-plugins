package org.hyperskill.courseFormat

import com.intellij.openapi.project.Project
import org.hyperskill.api.client.hsLessons
import org.hyperskill.api.objects.lesson.HSLesson
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.core.courseFormat.Node

class HyperskillTree(project: Project? = null, stepikApiClient: StepikApiClient? = null,
                     data: StudyObject? = StudyObject()) :
        Node(project, stepikApiClient, data) {
    
    override val childClass: Class<out Node>
        get() = HSLessonNode::class.java
    
    override val childDataClass: Class<out StudyObject>
        get() = HSLesson::class.java
    
    override val dataClass: Class<out StudyObject>
        get() = StudyObject::class.java
    
    override fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean {
        return true
    }
    
    override fun getCourseId(stepikApiClient: StepikApiClient): Long {
        return 0
    }
    
    override fun getChildDataList(stepikApiClient: StepikApiClient): List<StudyObject> {
        val objects = mutableListOf<HSLesson>()
        try {
            val lessonsIds = children.map { it.id }
                    .toHashSet()
            if (lessonsIds.isNotEmpty()) {
                stepikApiClient.hsLessons()
                        .get()
                        .id(lessonsIds.toList())
                        .execute()
                        .forEach { lesson ->
                            objects.add(lesson)
                        }
            }
        } catch (logged: StepikClientException) {
            logger.warn("A Hyperskill initialization don't is fully", logged)
        }
        
        return objects
    }
}
