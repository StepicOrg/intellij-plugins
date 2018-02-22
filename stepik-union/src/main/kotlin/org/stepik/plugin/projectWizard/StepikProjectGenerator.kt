package org.stepik.plugin.projectWizard

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectEx
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.core.ProjectGenerator
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.SupportedLanguages
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.courseFormat.StudyNodeFactory
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED
import org.stepik.core.metrics.MetricsStatus.TARGET_NOT_FOUND
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.currentUser
import org.stepik.plugin.projectWizard.CoursesList.COURSES
import java.util.Collections.emptyList
import java.util.concurrent.CompletableFuture

object StepikProjectGenerator : ProjectGenerator, Loggable {
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
        val projectManager = getProjectManager(project)
        if (projectManager == null) {
            Metrics.createProject(project, TARGET_NOT_FOUND)
            return
        }
        projectManager.projectRoot = projectRoot
        projectManager.createdBy = currentUser.id
        projectManager.defaultLang = defaultLang

        (project as ProjectEx).setProjectName(projectRoot!!.name)

        Metrics.createProject(project)
    }


    private var projectRoot: StudyNode? = null


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
