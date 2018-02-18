package org.stepik.core.ui

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import javafx.application.Platform
import javafx.stage.FileChooser
import org.stepik.api.objects.ObjectsContainer
import org.stepik.core.StepikProjectManager
import org.stepik.core.StudyUtils
import org.stepik.core.actions.SendAction
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StepType
import org.stepik.core.stepik.StepikAuthManager
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthState
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow
import org.stepik.core.testFramework.toolWindow.StepikTestToolWindowUtils
import org.stepik.core.ui.StepDescriptionUtils.getReply
import org.stepik.core.utils.NavigationUtils
import org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory
import org.stepik.plugin.actions.navigation.StudyNavigator
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.html.HTMLFormElement
import java.io.File
import java.io.IOException
import java.nio.file.Files

internal class FormListener(private val project: Project, private val browser: StudyBrowserWindow) : EventListener {

    override fun handleEvent(event: Event) {
        val domEventType = event.type
        if (EVENT_TYPE_SUBMIT == domEventType) {
            val form = event.target as HTMLFormElement
            handle(project, browser, form)
            event.preventDefault()
            event.stopPropagation()
        }
    }

    companion object : Loggable {
        const val EVENT_TYPE_SUBMIT = "submit"

        private fun getDataFromFile(stepNode: StepNode, project: Project): String? {
            val fileChooser = FileChooser()
            fileChooser.title = "Open file"
            val srcDirectory = getOrCreateSrcDirectory(project, stepNode, true)
            if (srcDirectory != null) {
                val initialDir = File(srcDirectory.path)
                fileChooser.initialDirectory = initialDir
            }
            val file = fileChooser.showOpenDialog(null)
            if (file != null) {
                try {
                    val lines = Files.readAllLines(file.toPath())
                    return lines.joinToString { "\n" }
                } catch (e: IOException) {
                    logger.warn(e)
                }
            }
            return null
        }

        fun getAttempt(project: Project, node: StepNode) {
            val stepikApiClient = authAndGetStepikApiClient(true)

            stepikApiClient.attempts()
                    .post<ObjectsContainer<*>>()
                    .step(node.id)
                    .executeAsync()
                    .whenComplete { attempts, e ->
                        if (attempts != null) {
                            node.cleanLastReply()
                            StepikProjectManager.updateSelection(project)
                        } else {
                            logger.warn(e)
                        }
                    }
        }

        private fun sendStep(
                project: Project,
                stepNode: StepNode,
                elements: Elements,
                type: StepType,
                attemptId: Long,
                data: String?) {
            val stepikApiClient = authAndGetStepikApiClient(true)

            val query = stepikApiClient.submissions()
                    .post()
                    .attempt(attemptId)
            val reply = getReply(stepNode, type, elements, data) ?: return

            query.reply(reply)
                    .executeAsync()
                    .whenComplete { submissions, e ->
                        val lesson = stepNode.parent
                        val lessonName = lesson?.name ?: ""

                        val resultWindows = arrayOfNulls<StepikTestResultToolWindow>(1)

                        val title = "$lessonName : ${stepNode.name}"
                        ApplicationManager.getApplication().invokeAndWait {
                            resultWindows[0] = StepikTestToolWindowUtils
                                    .showTestResultsToolWindow(project, title)
                        }

                        val resultWindow = resultWindows[0]!!
                        resultWindow.clear()
                        resultWindow.println("Test method: send to Stepik")

                        if (submissions == null) {
                            printError(e, resultWindow)
                            StepikProjectManager.updateSelection(project)
                            return@whenComplete
                        }

                        if (submissions.isEmpty) {
                            printError(e, resultWindow)
                            return@whenComplete
                        }

                        val submission = submissions.first
                        SendAction.checkStepStatus(project,
                                stepikApiClient,
                                stepNode,
                                submission.id,
                                resultWindow)
                    }
        }

        private fun printError(e: Throwable, resultWindow: StepikTestResultToolWindow) {
            resultWindow.println("Failed send step from browser", ConsoleViewContentType.ERROR_OUTPUT)
            resultWindow.println(e.message ?: "", ConsoleViewContentType.ERROR_OUTPUT)
            logger.warn("Failed send step from browser", e)
        }

        fun handle(
                project: Project,
                browser: StudyBrowserWindow,
                form: HTMLFormElement) {
            val root = StepikProjectManager.getProjectRoot(project) ?: return

            val node = StudyUtils.getStudyNode(root, form.action) as? StepNode ?: return

            val elements = Elements(form.elements)

            when (elements.action) {
                "get_first_attempt", "get_attempt" -> if (!elements.isLocked) {
                    getAttempt(project, node)
                }
                "submit" -> {
                    val type = StepType.of(elements.type)
                    val isFromFile = elements.isFromFile
                    val data = if (isFromFile) getDataFromFile(node, project) else null
                    val attemptId = elements.attemptId
                    sendStep(project, node, elements, type, attemptId, data)
                }
                "save_reply" -> {
                    val type = StepType.of(elements.type)
                    getReply(node, type, elements, null)
                }
                "login" -> {
                    val email = elements.getInputValue("email")
                    val password = elements.getInputValue("password")
                    browser.showLoadAnimation()
                    StepikAuthManager.authentication(email, password)
                            .whenComplete { state, _ ->
                                if (state != StepikAuthState.AUTH) {
                                    browser.callFunction("setErrorMessage", "Wrong email or password")
                                }
                                browser.hideLoadAnimation()
                            }
                }
                "next_step" -> {
                    val targetNode = StudyNavigator.nextLeaf(node) ?: return

                    Platform.runLater { NavigationUtils.navigate(project, targetNode) }
                }
                else -> browser.hideLoadAnimation()
            }
        }
    }
}
