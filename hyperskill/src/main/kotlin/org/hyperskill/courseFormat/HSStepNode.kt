package org.hyperskill.courseFormat

import com.intellij.openapi.project.Project
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.steps.Step
import org.stepik.core.courseFormat.StepNode

class HSStepNode(project: Project? = null, stepikApiClient: StepikApiClient? = null, data: StudyObject? = null) :
        StepNode(project, stepikApiClient, data) {
    
    @XStreamOmitField
    override var assignment: Long? = null
        get() = 0
    
    override val childClass: Class<StepNode>
        get() = StepNode::class.java
    
    override val childDataClass: Class<Step>
        get() = Step::class.java
    
    override val dataClass: Class<Step>
        get() = Step::class.java
    
    override fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean {
        try {
            val data = stepikApiClient.steps()
                               .get()
                               .id(id)
                               .execute()
                               .firstOrNull()
                       ?: Step().also { it.id = id }
            
            val oldData = this.data
            this.data = data
            return oldData.updateDate != data.updateDate
        } catch (logged: StepikClientException) {
            logger.warn(String.format("Failed step lesson data id=%d", id), logged)
        }
        
        return true
    }
    
    override fun getCourseId(stepikApiClient: StepikApiClient): Long {
        return 0
    }
}
