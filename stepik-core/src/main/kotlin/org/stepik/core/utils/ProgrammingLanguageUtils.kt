package org.stepik.core.utils

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesUtil.doMoveFile
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.queries.Order
import org.stepik.core.EduNames
import org.stepik.core.EduNames.HIDE
import org.stepik.core.StudyUtils.getStudyNode
import org.stepik.core.SupportedLanguages
import org.stepik.core.SupportedLanguages.Companion.langOfName
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.currentUser
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.metrics.Metrics
import java.io.IOException

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

        if (mainFile != null && currentLang === language) {
            openFile(project, mainFile)
            return
        }

        if (mainFile != null && currentMainFileName == language.mainFileName) {
            targetStepNode.currentLang = language
            openFile(project, mainFile)
            Metrics.switchLanguage(project, targetStepNode)
            return
        }

        val src = getOrCreateSrcPsiDirectory(project, targetStepNode) ?: return

        val hide = getOrCreatePsiDirectory(project, src, HIDE) ?: return

        var second = findFile(src, language.mainFileName)
        val moveSecond = second == null
        if (moveSecond) {
            second = getOrCreateMainFile(project, hide.virtualFile, language, targetStepNode)
            if (second == null) {
                logger.error("Can't create Main file: ${language.mainFileName}")
                return
            }
        }

        var first = findFile(hide, currentMainFileName)
        var moveFirst = first == null

        if (moveFirst) {
            first = findFile(src, currentMainFileName)
            moveFirst = !second!!.isEquivalentTo(first)
        }

        targetStepNode.currentLang = language
        val needClose = getNeedCloseFiles(project, targetStepNode)

        openFile(project, second!!.virtualFile)

        getApplication().invokeAndWait {
            val editorManager = FileEditorManager.getInstance(project)
            needClose.forEach { editorManager.closeFile(it) }

            exchangeFiles(src, hide, first, second, moveFirst, moveSecond)

            ProjectView.getInstance(project).selectPsiElement(second, false)
        }

        Metrics.switchLanguage(project, targetStepNode)
    }

    private fun openFile(project: Project, file: VirtualFile) {
        getApplication().invokeAndWait {
            FileEditorManager.getInstance(project).openFile(file, true)
        }
    }

    private fun findFile(parent: PsiDirectory, name: String): PsiFile? {
        return getApplication().runReadAction(Computable {
            parent.findFile(name)
        })
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

        getApplication().runWriteAction {
            if (moveFirst && first != null) {
                doMoveFile(first, hide)
            }

            if (moveSecond) {
                doMoveFile(second, src)
            }
        }
    }

    private fun getNeedCloseFiles(project: Project, targetStepNode: StepNode): List<VirtualFile> {
        project.saveAllDocuments()
        return FileEditorManager.getInstance(project)
                .openFiles
                .filter {
                    getStudyNode(project, it) == targetStepNode
                }
    }

    private fun getOrCreateMainFile(
            project: Project,
            parent: VirtualFile,
            language: SupportedLanguages,
            stepNode: StepNode): PsiFile? {
        val fileName = language.mainFileName
        var file = parent.findChild(fileName)

        if (file == null) {
            getApplication().runWriteActionAndWait {
                try {
                    file = parent.createChildData(null, fileName)
                    var template: String? = null

                    val stepikApiClient = authAndGetStepikApiClient()
                    val user = currentUser
                    if (!user.isGuest) {
                        try {
                            stepikApiClient.submissions()
                                    .get()
                                    .user(user.id)
                                    .order(Order.DESC)
                                    .step(stepNode.id)
                                    .execute()
                                    .firstOrNull {
                                        langOfName(it.reply.language) === language
                                    }?.also {
                                        template = it.reply.code
                                    }
                        } catch (e: StepikClientException) {
                            logger.warn(e)
                        }
                    }

                    if (template == null) {
                        template = stepNode.getTemplate(language)
                    }

                    file!!.setBinaryContent(template!!.toByteArray())
                } catch (e: IOException) {
                    file = null
                }
            }
        }

        return getApplication().runReadAction(Computable {
            if (file != null) {
                PsiManager.getInstance(project).findFile(file!!)
            } else {
                null
            }
        })
    }
}
