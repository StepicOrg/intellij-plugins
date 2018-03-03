package org.stepik.core.courseFormat

import com.intellij.openapi.project.Project
import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.courses.Course
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.lessons.Lesson
import org.stepik.api.objects.sections.Section
import org.stepik.api.objects.steps.Step


object StudyNodeFactory {
    fun createTree(
            project: Project,
            stepikApiClient: StepikApiClient,
            data: StudyObject): StudyNode? {
        return when (data) {
            is Course -> CourseNode(project, stepikApiClient, data)
            is Section -> SectionNode(project, stepikApiClient, data)
            is Lesson -> LessonNode(project, stepikApiClient, CompoundUnitLesson(lesson = data))
            is CompoundUnitLesson -> LessonNode(project, stepikApiClient, data)
            is Step -> StepNode(project, stepikApiClient, data)
            else -> null
        }
    }
}
