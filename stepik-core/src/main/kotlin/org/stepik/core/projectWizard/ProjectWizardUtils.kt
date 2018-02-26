package org.stepik.core.projectWizard

import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.courses.Course
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.sections.Section
import org.stepik.api.objects.steps.Step
import org.stepik.core.SupportedLanguages
import org.stepik.core.auth.StepikAuthManager
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory
import java.io.File


object ProjectWizardUtils : Loggable {

    private fun findNonExistingFileName(searchDirectory: String, preferredName: String): String {
        var fileName = preferredName
        var idx = 1

        while (File(searchDirectory, fileName).exists()) {
            fileName = "${preferredName}_${idx++}"
        }

        return fileName
    }

    fun getProjectDefaultName(projectDirectory: String, studyObject: StudyObject): String? {
        val projectName = when (studyObject) {
            is Course -> "course"
            is CompoundUnitLesson -> "lesson"
            is Section -> "section"
            is Step -> "step"
            else -> "unknown"
        } + studyObject.id

        return findNonExistingFileName(projectDirectory, projectName)
    }

    fun enrollmentCourse(studyObject: StudyObject): Boolean {
        val stepikApiClient = StepikAuthManager.authAndGetStepikApiClient()
        if (!isAuthenticated) {
            return false
        }

        if (studyObject is Course) {
            enrollment(stepikApiClient, studyObject)
        } else if (studyObject is CompoundUnitLesson) {
            enrollment(stepikApiClient, studyObject)
        }

        return true
    }

    private fun enrollment(stepikApiClient: StepikApiClient, studyObject: CompoundUnitLesson) {
        val sectionId = studyObject.unit.section
        if (sectionId != 0) {
            try {
                val sections = stepikApiClient.sections()
                        .get()
                        .id(sectionId)
                        .execute()

                if (sections.isNotEmpty) {
                    val courseId = sections.first().id

                    if (courseId != 0L) {
                        val courses = stepikApiClient.courses()
                                .get()
                                .id(courseId)
                                .execute()
                        if (courses.isNotEmpty) {
                            enrollment(stepikApiClient, courses.first())
                        }
                    }
                }
            } catch (e: StepikClientException) {
                val messageTemplate = "Can't enrollment on a lesson: id = %s, name = %s"
                val message = String.format(messageTemplate, studyObject.id, studyObject.title)
                logger.error(message, e)
            }
        }
    }

    private fun enrollment(stepikApiClient: StepikApiClient, studyObject: StudyObject) {
        try {
            stepikApiClient.enrollments()
                    .post()
                    .course(studyObject.id)
                    .execute()
        } catch (e: StepikClientException) {
            val messageTemplate = "Can't enrollment on a course: id = %s, name = %s"
            val message = String.format(messageTemplate, studyObject.id, studyObject.title)
            logger.error(message, e)
        }
    }

    fun createSubDirectories(
            project: Project,
            defaultLanguage: SupportedLanguages,
            root: StudyNode,
            model: ModifiableModuleModel?) {
        root.children
                .forEach { child ->
                    FileUtil.createDirectory(File(project.basePath, child.path))
                    if (child is StepNode) {
                        child.currentLang = defaultLanguage
                        getOrCreateSrcDirectory(project, child, false, model)
                    } else {
                        createSubDirectories(project, defaultLanguage, child, model)
                    }
                }
    }
}
