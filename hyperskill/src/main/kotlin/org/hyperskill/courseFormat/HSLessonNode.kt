package org.hyperskill.courseFormat

import com.intellij.openapi.project.Project
import org.hyperskill.api.client.hsLessons
import org.hyperskill.api.objects.lesson.HSLesson
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.steps.Step
import org.stepik.core.EduNames
import org.stepik.core.courseFormat.Node

open class HSLessonNode(project: Project? = null, stepikApiClient: StepikApiClient? = null, data: StudyObject? = null) :
        Node(project, stepikApiClient, data) {
    
    override val childClass: Class<HSStepNode>
        get() = HSStepNode::class.java
    
    override val childDataClass: Class<Step>
        get() = Step::class.java
    
    override val dataClass: Class<HSLesson>
        get() = HSLesson::class.java
    
    override val directoryPrefix
        get() = EduNames.LESSON
    
    override fun getChildDataList(stepikApiClient: StepikApiClient): List<StudyObject> {
        try {
            return stepikApiClient.steps()
                    .get()
                    .lesson((data as HSLesson).stepikId)
                    .execute()
                    .items
        } catch (logged: StepikClientException) {
            logger.warn("A lesson initialization don't is fully", logged)
        }
        
        return emptyList()
    }
    
    override fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean {
        try {
            val lessons = stepikApiClient.hsLessons()
                    .get()
                    .id(id)
                    .execute()
            
            this.data = lessons.firstOrDefault(HSLesson().apply { this.id = id })
        } catch (logged: StepikClientException) {
            logger.warn(String.format("Failed load lesson data id=%d", id), logged)
        }
        
        return true
    }
    
    override fun getCourseId(stepikApiClient: StepikApiClient): Long = 0
}
