package org.stepik.core.courseFormat

import com.intellij.openapi.project.Project
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.lessons.Lesson
import org.stepik.api.objects.steps.Step
import org.stepik.core.core.EduNames

open class LessonNode(project: Project? = null, stepikApiClient: StepikApiClient? = null, data: StudyObject? = null) :
        Node(project, stepikApiClient, data) {
    private var courseId: Long = 0

    override val childClass: Class<StepNode>
        get() = StepNode::class.java

    override val childDataClass: Class<Step>
        get() = Step::class.java

    override val dataClass: Class<CompoundUnitLesson>
        get() = CompoundUnitLesson::class.java

    @XStreamOmitField
    override val directoryPrefix = EduNames.LESSON

    override fun getChildDataList(stepikApiClient: StepikApiClient): List<StudyObject> {
        try {
            val stepsIds = (data as CompoundUnitLesson).lesson.steps

            if (!stepsIds.isEmpty()) {
                return stepikApiClient.steps()
                        .get()
                        .id(stepsIds)
                        .execute().steps
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

            val lesson: Lesson
            if (!lessons.isEmpty) {
                lesson = lessons.first
            } else {
                lesson = Lesson()
                lesson.id = id
            }
            data.setLesson(lesson)

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
            val sections = stepikApiClient.sections()
                    .get()
                    .id(sectionId)
                    .execute()
            if (sections.isEmpty) {
                return 0
            }
            courseId = sections.first.course.toLong()
            return courseId
        } catch (ignored: StepikClientException) {
        }

        return 0
    }
}
