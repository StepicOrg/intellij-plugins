package org.stepik.core.utils

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesUtil
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.queries.Order
import org.stepik.core.StudyUtils
import org.stepik.core.SupportedLanguages
import org.stepik.core.common.Loggable
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.SUCCESSFUL
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.currentUser
import org.stepik.core.utils.ProjectFilesUtils.getOrCreatePsiDirectory
import org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcPsiDirectory
import java.io.IOException
import java.util.*

object ProgrammingLanguageUtils : Loggable {

    fun switchProgrammingLanguage(
            project: Project,
            targetStepNode: StepNode,
            language: SupportedLanguages) {
        switchLanguage(project, targetStepNode, language)
        targetStepNode.currentLang.runner.updateRunConfiguration(project, targetStepNode)
    }

    private fun switchLanguage(
            project: Project,
            targetStepNode: StepNode,
            language: SupportedLanguages) {
        if (!targetStepNode.supportedLanguages.contains(language)) {
            return
        }

        val currentLang = targetStepNode.currentLang
        val currentMainFileName = currentLang.mainFileName

        val mainFilePath = arrayOf(targetStepNode.path, EduNames.SRC, currentMainFileName).joinToString("/")
        val mainFile = project.baseDir.findFileByRelativePath(mainFilePath)

        val mainFileExists = mainFile != null

        if (currentLang === language && mainFileExists) {
            return
        }

        if (currentMainFileName == language.mainFileName && mainFileExists) {
            targetStepNode.setCurrentLang(language)
            Metrics.switchLanguage(project, targetStepNode, SUCCESSFUL)
            return
        }

        val src = getOrCreateSrcPsiDirectory(project, targetStepNode) ?: return

        val hide = getOrCreatePsiDirectory(project, src, EduNames.HIDE) ?: return

        var second = findFile(src, language.mainFileName)
        val moveSecond = second == null
        if (moveSecond) {
            second = getOrCreateMainFile(project, hide.virtualFile, language, targetStepNode)
            if (second == null) {
                logger.error("Can't create Main file: " + language.mainFileName)
                return
            }
        }

        var first = findFile(hide, currentMainFileName)
        var moveFirst = first == null

        if (moveFirst) {
            first = findFile(src, currentMainFileName)
            moveFirst = !second!!.isEquivalentTo(first)
        }

        targetStepNode.setCurrentLang(language)
        val needClose = closeStepNodeFile(project, targetStepNode)

        val finalSecond = second
        val finalFirst = first
        val finalMoveFirst = moveFirst
        ApplicationManager.getApplication()
                .invokeAndWait {
                    FileEditorManager.getInstance(project).openFile(finalSecond!!.virtualFile, true)
                    val editorManager = FileEditorManager.getInstance(project)
                    needClose.forEach { editorManager.closeFile(it) }

                    exchangeFiles(src, hide, finalFirst, finalSecond, finalMoveFirst, moveSecond)

                    ProjectView.getInstance(project).selectPsiElement(finalSecond, false)
                }

        Metrics.switchLanguage(project, targetStepNode, SUCCESSFUL)
    }

    private fun findFile(parent: PsiDirectory, name: String): PsiFile? {
        return ApplicationManager.getApplication()
                .runReadAction(Computable { parent.findFile(name) })
    }

    private fun exchangeFiles(
            src: PsiDirectory,
            hide: PsiDirectory,
            first: PsiFile?,
            second: PsiFile,
            moveFirst: Boolean,
            moveSecond: Boolean) {
        if (!moveFirst && !moveSecond) {
            return
        }

        ApplicationManager.getApplication().runWriteAction {
            if (moveFirst && first != null) {
                MoveFilesOrDirectoriesUtil.doMoveFile(first, hide)
            }

            if (moveSecond) {
                MoveFilesOrDirectoriesUtil.doMoveFile(second, src)
            }
        }
    }

    private fun closeStepNodeFile(
            project: Project,
            targetStepNode: StepNode): ArrayList<VirtualFile> {
        val documentManager = FileDocumentManager.getInstance()
        val needClose = ArrayList<VirtualFile>()
        for (file in FileEditorManager.getInstance(project).openFiles) {
            if (StudyUtils.getStudyNode(project, file) !== targetStepNode) {
                continue
            }
            val document = ApplicationManager.getApplication()
                    .runReadAction(Computable { documentManager.getDocument(file) }
                    ) ?: continue
            ApplicationManager.getApplication().invokeAndWait { documentManager.saveDocument(document) }
            needClose.add(file)
        }

        return needClose
    }

    private fun getOrCreateMainFile(
            project: Project,
            parent: VirtualFile,
            language: SupportedLanguages,
            stepNode: StepNode): PsiFile? {
        val fileName = language.mainFileName
        var file = parent.findChild(fileName)

        val application = ApplicationManager.getApplication()
        if (file == null) {
            application.invokeAndWait {
                application.runWriteAction {
                    try {
                        file = parent.createChildData(null, fileName)
                        var template: String? = null

                        val stepikApiClient = authAndGetStepikApiClient()
                        val user = currentUser
                        if (!user.isGuest) {
                            try {
                                val submissions = stepikApiClient.submissions()
                                        .get()
                                        .user(user.id)
                                        .order(Order.DESC)
                                        .step(stepNode.id)
                                        .execute()

                                if (!submissions.isEmpty) {
                                    val lastSubmission = submissions.items
                                            .stream()
                                            .filter { submission ->
                                                SupportedLanguages.langOfName(submission
                                                        .reply
                                                        .language) === language
                                            }
                                            .limit(1)
                                            .findFirst()
                                    if (lastSubmission.isPresent) {
                                        template = lastSubmission.get().reply.code
                                    }
                                }
                            } catch (e: StepikClientException) {
                                logger.warn(e)
                            }

                        }

                        if (template == null) {
                            template = stepNode.getTemplate(language)
                        }

                        file!!.setBinaryContent(template.toByteArray())
                    } catch (e: IOException) {
                        file = null
                    }
                }
            }
        }

        return application.runReadAction(Computable {
            if (file != null) {
                PsiManager.getInstance(project).findFile(file!!)
            } else {
                null
            }
        }
        )
    }
}
