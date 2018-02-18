package org.stepik.core.projectWizard

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectEx
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.courses.Course
import org.stepik.core.StepikProjectManager
import org.stepik.core.SupportedLanguages
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.courseFormat.StudyNodeFactory
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED
import org.stepik.core.metrics.MetricsStatus.SUCCESSFUL
import org.stepik.core.metrics.MetricsStatus.TARGET_NOT_FOUND
import org.stepik.core.projectWizard.CoursesList.COURSES
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.currentUser
import java.util.Collections.emptyList
import java.util.concurrent.CompletableFuture

object StepikProjectGenerator : Loggable {
    var defaultLang = SupportedLanguages.INVALID

    fun createCourseNodeUnderProgress(project: Project, data: StudyObject) {
        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously({
                    if (data.id == 0L) {
                        logger.warn("Failed to get a course")
                        Metrics.createProject(project, DATA_NOT_LOADED)
                        return@runProcessWithProgressSynchronously
                    }

                    val indicator = ProgressManager.getInstance().progressIndicator
                    indicator.isIndeterminate = true

                    val stepikApiClient = authAndGetStepikApiClient()
                    projectRoot = StudyNodeFactory.createTree(project, stepikApiClient, data)
                }, "Creating Project", true, project)
    }

    fun generateProject(project: Project) {
        val stepikProjectManager = StepikProjectManager.getInstance(project)
        if (stepikProjectManager == null) {
            Metrics.createProject(project, TARGET_NOT_FOUND)
            return
        }
        stepikProjectManager.setRootNode(projectRoot)
        stepikProjectManager.setCreatedBy(currentUser.id)
        stepikProjectManager.defaultLang = defaultLang

        (project as ProjectEx).setProjectName(projectRoot!!.name)

        Metrics.createProject(project, SUCCESSFUL)
    }


    private var projectRoot: StudyNode<*, *>? = null


    fun getCourses(programmingLanguage: SupportedLanguages): CompletableFuture<List<StudyObject>> {
        return CompletableFuture.supplyAsync {
            val coursesIds = COURSES.getOrDefault(programmingLanguage, emptyList())

            if (coursesIds.isEmpty()) {
                return@supplyAsync emptyList<StudyObject>()
            }

            val stepikApiClient = authAndGetStepikApiClient()

            try {
                return@supplyAsync stepikApiClient.courses()
                        .get()
                        .id(coursesIds)
                        .execute()
                        .courses
                        .map { course -> course }
                        .sortedBy { coursesIds.indexOf(it.id) }
            } catch (e: StepikClientException) {
                logger.warn("Failed get courses", e)
                return@supplyAsync emptyList<StudyObject>()
            }
        }
    }
}

val EMPTY_STUDY_OBJECT = initEmptyStudyNode()

private fun initEmptyStudyNode(): StudyObject {
    val course = Course()
    course.setTitle("Empty")
    course.setDescription("Please, press refresh button")
    return course
}
