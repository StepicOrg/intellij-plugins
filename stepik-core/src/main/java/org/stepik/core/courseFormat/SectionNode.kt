package org.stepik.core.courseFormat

import com.intellij.openapi.project.Project
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.sections.Section
import org.stepik.api.objects.units.Unit
import org.stepik.core.core.EduNames
import java.util.*


class SectionNode(project: Project? = null, stepikApiClient: StepikApiClient? = null, data: StudyObject? = null) :
        Node(project, stepikApiClient, data) {

    override val childClass: Class<LessonNode>
        get() = LessonNode::class.java

    override val childDataClass: Class<CompoundUnitLesson>
        get() = CompoundUnitLesson::class.java

    override val dataClass: Class<Section>
        get() = Section::class.java

    @XStreamOmitField
    override val directoryPrefix = EduNames.SECTION

    override fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean {
        try {
            val sections = stepikApiClient.sections()
                    .get()
                    .id(id)
                    .execute()

            val data: Section

            if (!sections.isEmpty) {
                data = sections.first
            } else {
                data = Section()
                data.id = id
            }

            val oldData = this.data as Section
            this.data = data
            return oldData.updateDate != data.updateDate
        } catch (logged: StepikClientException) {
            logger.warn(String.format("Failed load section data id=%d", id), logged)
        }

        return true
    }

    override fun getCourseId(stepikApiClient: StepikApiClient): Long {
        return (data as Section).course.toLong()
    }

    override fun getChildDataList(stepikApiClient: StepikApiClient): List<StudyObject> {
        val objects = ArrayList<CompoundUnitLesson>()
        try {
            val unitsIds: List<Long>
            val data = data as Section
            unitsIds = data.units

            if (!unitsIds.isEmpty()) {
                val size = unitsIds.size
                val list = unitsIds.toTypedArray()
                var start = 0
                var end: Int
                while (start < size) {
                    end = start + 20
                    if (end > size) {
                        end = size
                    }
                    val part = list.copyOfRange(start, end)
                    start = end
                    val units = stepikApiClient.units()
                            .get()
                            .id(*part)
                            .execute()

                    val unitsMap = HashMap<Long, Unit>()

                    val lessonsIds = ArrayList<Long>()

                    units.units.forEach { unit ->
                        val lessonId = unit.lesson.toLong()
                        lessonsIds.add(lessonId)
                        unitsMap[lessonId] = unit
                    }

                    val lessons = stepikApiClient.lessons()
                            .get()
                            .id(lessonsIds)
                            .execute()

                    lessons.lessons
                            .forEach { lesson ->
                                objects.add(CompoundUnitLesson(unitsMap[lesson.id],
                                        lesson))
                            }
                }
            }
        } catch (logged: StepikClientException) {
            logger.warn("A section initialization don't is fully", logged)
        }

        return objects
    }
}
