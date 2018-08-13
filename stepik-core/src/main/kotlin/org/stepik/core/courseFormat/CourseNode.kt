package org.stepik.core.courseFormat

import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.courses.Course
import org.stepik.api.objects.sections.Section
import org.stepik.api.objects.sections.Sections
import org.stepik.api.objects.users.User

class CourseNode(project: Project? = null, stepikApiClient: StepikApiClient? = null, data: StudyObject? = null) :
        Node(project, stepikApiClient, data) {
    
    private var authors: List<User>? = null
    
    override val childClass: Class<SectionNode>
        get() = SectionNode::class.java
    
    override val childDataClass: Class<Section>
        get() = Section::class.java
    
    override val dataClass: Class<Course>
        get() = Course::class.java
    
    override fun getChildDataList(stepikApiClient: StepikApiClient): List<StudyObject> {
        var sections = Sections()
        try {
            val data = data as Course
            val sectionsIds = data.sections
            if (!sectionsIds.isEmpty()) {
                sections = stepikApiClient.sections()
                        .get()
                        .id(sectionsIds)
                        .execute()
            }
        } catch (logged: StepikClientException) {
            logger.warn("A course initialization don't is fully", logged)
        }
        
        return sections.sections
    }
    
    override fun beforeInit() {
        authors = null
    }
    
    override fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean {
        try {
            val courses = stepikApiClient.courses()
                    .get()
                    .id(id)
                    .execute()
            
            val data = courses.firstOrDefault(Course().apply { this.id = id })
            
            val oldData = this.data as Course
            this.data = data
            return oldData.updateDate != data.updateDate
        } catch (logged: StepikClientException) {
            logger.warn(String.format("Failed load course data id=%d", id), logged)
        }
        
        return true
    }
    
    override fun getCourseId(stepikApiClient: StepikApiClient): Long {
        return id
    }
}
