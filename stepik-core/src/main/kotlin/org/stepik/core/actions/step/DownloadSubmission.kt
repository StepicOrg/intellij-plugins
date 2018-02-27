package org.stepik.core.actions.step

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupListener
import com.intellij.openapi.ui.popup.LightweightWindowEvent
import com.intellij.ui.components.JBList
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.submissions.Submission
import org.stepik.api.queries.Order
import org.stepik.core.SupportedLanguages
import org.stepik.core.actions.getShortcutText
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.currentUser
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyStatus
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED
import org.stepik.core.metrics.MetricsStatus.EMPTY_SOURCE
import org.stepik.core.metrics.MetricsStatus.TARGET_NOT_FOUND
import org.stepik.core.metrics.MetricsStatus.USER_CANCELED
import org.stepik.core.utils.containsDirectives
import org.stepik.core.utils.getOrCreateSrcDirectory
import org.stepik.core.utils.isCanceled
import org.stepik.core.utils.replaceCode
import org.stepik.core.utils.uncommentAmbientCode
import java.text.SimpleDateFormat
import javax.swing.JList


class DownloadSubmission : CodeQuizAction(TEXT, DESCRIPTION, AllStepikIcons.ToolWindow.download) {

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        getApplication().executeOnPooledThread {
            downloadSubmission(project)
        }
    }

    private fun downloadSubmission(project: Project) {
        val stepNode = getCurrentCodeStepNode(project) ?: return

        val stepikApiClient = authAndGetStepikApiClient(true)
        if (!isAuthenticated) {
            return
        }

        val submissions = ProgressManager.getInstance()
                .run(object : Task.WithResult<List<Submission>, RuntimeException>(project, "Download submission", true) {
                    @Throws(RuntimeException::class)
                    override fun compute(progressIndicator: ProgressIndicator): List<Submission>? {
                        progressIndicator.isIndeterminate = true
                        val parent = stepNode.parent
                        val lessonName = parent?.name ?: ""
                        progressIndicator.text = lessonName
                        progressIndicator.text2 = stepNode.name
                        val submissions = getSubmissions(stepikApiClient, stepNode)

                        if (isCanceled) {
                            Metrics.downloadAction(project, stepNode, USER_CANCELED)
                            return null
                        }

                        if (submissions == null) {
                            Metrics.downloadAction(project, stepNode, DATA_NOT_LOADED)
                            return emptyList()
                        }

                        val currentLang = stepNode.currentLang

                        return filterSubmissions(submissions, currentLang)
                    }
                }) ?: return

        getApplication().invokeAndWait { showPopup(project, stepNode, submissions) }
    }

    private fun getSubmissions(
            stepikApiClient: StepikApiClient,
            stepNode: StepNode): List<Submission>? {
        return try {
            stepikApiClient.submissions()
                    .get()
                    .step(stepNode.id)
                    .user(currentUser.id)
                    .order(Order.DESC)
                    .execute()
                    .items
        } catch (e: StepikClientException) {
            logger.warn("Failed get submissions", e)
            null
        }
    }

    private fun filterSubmissions(
            submissions: List<Submission>,
            currentLang: SupportedLanguages): List<Submission> {
        return submissions.filter {
            SupportedLanguages.langOfName(it.reply.language).canUpgradedTo(currentLang)
        }
    }

    private fun showPopup(
            project: Project,
            stepNode: StepNode,
            submissions: List<Submission>) {
        val popupFactory = JBPopupFactory.getInstance()

        val builder = if (!submissions.isEmpty()) {
            val list = JBList(submissions.map { SubmissionDecorator(it) })
            popupFactory.createListPopupBuilder(list)
                    .addListener(Listener(list, project, stepNode))
        } else {
            popupFactory.createListPopupBuilder(JBList<String>("No submissions"))
        }

        val popup = builder.setTitle("Choose submission").createPopup()
        popup.showCenteredInCurrentWindow(project)
    }

    private fun loadSubmission(
            project: Project,
            stepNode: StepNode,
            submission: Submission) {

        val fileName = stepNode.currentLang.mainFileName

        val src = getOrCreateSrcDirectory(project, stepNode, true)
        if (src == null) {
            Metrics.downloadAction(project, stepNode, TARGET_NOT_FOUND)
            return
        }

        val mainFile = src.findChild(fileName)
        if (mainFile == null) {
            Metrics.downloadAction(project, stepNode, TARGET_NOT_FOUND)
            return
        }

        val code = submission.reply.code

        CommandProcessor.getInstance().executeCommand(project,
                {
                    ApplicationManager.getApplication().runWriteAction {
                        val documentManager = FileDocumentManager.getInstance()
                        val document = documentManager.getDocument(mainFile)

                        if (document != null) {
                            val language = SupportedLanguages.langOfName(submission.reply.language)
                            val text = if (containsDirectives(code, language)) {
                                uncommentAmbientCode(code, language)
                            } else {
                                replaceCode(document.text, code, language)
                            }
                            document.setText(text)

                            val status = StudyStatus.of(submission.status)
                            stepNode.status = status
                            FileEditorManager.getInstance(project).openFile(mainFile, true)
                            ProjectView.getInstance(project).refresh()
                            Metrics.downloadAction(project, stepNode)

                            language.runner.updateRunConfiguration(project, stepNode)
                        }
                    }
                },
                "Download submission",
                "Download submission")
    }

    private class SubmissionDecorator internal constructor(internal val submission: Submission) {

        override fun toString(): String {
            val localTime = timeOutFormat.format(submission.time)
            return String.format("#%d %-7s %s", submission.id, submission.status, localTime)
        }

        companion object {
            private val timeOutFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
        }
    }

    private inner class Listener internal constructor(
            private val list: JList<SubmissionDecorator>,
            private val project: Project,
            private val stepNode: StepNode) : JBPopupListener {

        override fun beforeShown(event: LightweightWindowEvent) {}

        override fun onClosed(event: LightweightWindowEvent) {
            when {
                !event.isOk -> {
                    Metrics.downloadAction(project, stepNode, USER_CANCELED)
                }
                list.isSelectionEmpty -> {
                    Metrics.downloadAction(project, stepNode, EMPTY_SOURCE)
                }
                else -> {
                    val submission = list.selectedValue?.submission

                    if (submission == null) {
                        Metrics.downloadAction(project, stepNode, EMPTY_SOURCE)
                        return
                    }

                    loadSubmission(project, stepNode, submission)
                }
            }

        }
    }

    companion object {
        private const val ACTION_ID = "STEPIK.DownloadSubmission"
        private const val SHORTCUT = "ctrl alt pressed PAGE_DOWN"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Download submission from the List ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Download submission from the List"
    }
}
