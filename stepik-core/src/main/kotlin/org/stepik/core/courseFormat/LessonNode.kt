package org.stepik.core.courseFormat

import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.lessons.Lesson
import org.stepik.api.objects.steps.Step
import org.stepik.core.EduNames

open class LessonNode(project: Project? = null, stepikApiClient: StepikApiClient? = null, data: StudyObject? = null) :
        Node(project, stepikApiClient, data) {
    private var courseId: Long = 0

    override val childClass: Class<StepNode>
        get() = StepNode::class.java

    override val childDataClass: Class<Step>
        get() = Step::class.java

    override val dataClass: Class<CompoundUnitLesson>
        get() = CompoundUnitLesson::class.java

    override val directoryPrefix
        get() = EduNames.LESSON

    override fun getChildDataList(stepikApiClient: StepikApiClient): List<StudyObject> {
        try {
            val stepsIds = (data as CompoundUnitLesson).lesson.steps

            if (!stepsIds.isEmpty()) {
                return stepikApiClient.steps()
                        .get()
                        .id(stepsIds)
                        .execute().items
            }
        } catch (logged: StepikClientException) {
            logger.warn("A lesson initialization don't is fully", logged)
        }

        return emptyList()
    }

    override fun beforeInit() {
        courseId = 0
    }

    override fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean {
        try {
            val data = data as CompoundUnitLesson

            val updateDate = data.updateDate

            val lessons = stepikApiClient.lessons()
                    .get()
                    .id(id)
                    .execute()

            data.lesson = lessons.firstOrDefault(Lesson().apply { this.id = id })

            return updateDate != data.updateDate
        } catch (logged: StepikClientException) {
            logger.warn(String.format("Failed load lesson data id=%d", id), logged)
        }

        return true
    }

    override fun getCourseId(stepikApiClient: StepikApiClient): Long {
        val parent = parent
        if (parent != null) {
            return parent.getCourseId(stepikApiClient)
        }

        if (courseId != 0L) {
            return courseId
        }

        val sectionId = (data as CompoundUnitLesson).unit.section
        if (sectionId == 0) {
            return 0
        }

        try {
            val section = stepikApiClient.sections()
                    .get()
                    .id(sectionId)
                    .execute()
                    .firstOrNull()
                    ?: return 0
            courseId = section.course.toLong()
            return courseId
        } catch (e: StepikClientException) {
            logger.warn(e)
        }

        return 0
    }
}
