package org.stepik.core.testFramework.runners

import com.intellij.execution.RunManager
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.EduNames
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.createDirectory
import org.stepik.core.testFramework.processes.TestProcess
import org.stepik.core.utils.getTextUnderDirectives
import org.stepik.core.utils.replaceCode
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.PrintStream
import java.util.concurrent.TimeUnit

interface Runner : Loggable {

    fun updateRunConfiguration(project: Project, stepNode: StepNode) {
        ApplicationManager.getApplication().invokeLater {
            RunManager.getInstance(project).selectedConfiguration = null
        }
    }

    fun createTestProcess(project: Project, stepNode: StepNode, mainFilePath: String): TestProcess =
            TestProcess(project, stepNode, mainFilePath)

    fun testSamples(project: Project,
                    stepNode: StepNode,
                    input: String,
                    assertion: (String) -> Boolean,
                    mainFilePath: String? = null,
                    testClass: Boolean = false): TestResult {
        val mainFile = mainFilePath ?: getMainFilePath(project, stepNode)?.path ?: return NO_PROCESS
        val process = createTestProcess(project, stepNode, mainFile).start(testClass) ?: return NO_PROCESS

        writeToProcessInput(process, input)

        val success = process.waitFor(stepNode.limit.time.toLong(), TimeUnit.SECONDS)

        if (!success) {
            process.destroyForcibly()
            return TIME_LEFT
        }

        val actualOutput = readProcessOutput(process)
        val errorOutput = readProcessErrorOutput(process)

        val actual = actualOutput.toString()
        val errorString = errorOutput.toString()
        val score = process.exitValue() == 0 && assertion(actual)
        val cause: ExitCause
        cause = if (score) {
            ExitCause.CORRECT
        } else {
            ExitCause.WRONG
        }
        return TestResult(score, actual, cause, errorString)
    }

    fun writeToProcessInput(process: Process, input: String) {
        val outputStream = PrintStream(BufferedOutputStream(process.outputStream))
        outputStream.print("$input\n")
        outputStream.flush()
    }

    fun readProcessOutput(stream: InputStream): StringBuffer {
        val inputStream = BufferedInputStream(stream)
        val actualOutput = StringBuffer()
        var b = inputStream.read()
        while (b != -1) {
            actualOutput.append(b.toChar())
            b = inputStream.read()
        }
        return actualOutput
    }

    fun readProcessOutput(process: Process): StringBuffer = readProcessOutput(process.inputStream)

    fun readProcessErrorOutput(process: Process): StringBuffer = readProcessOutput(process.errorStream)

    fun getMainFilePath(project: Project, stepNode: StepNode): VirtualFile? {
        val stepDirectory = project.baseDir.findFileByRelativePath(stepNode.path) ?: return null
        return stepDirectory.let {
            return@let it.findChild(EduNames.SRC)
        }?.let {
                    return@let it.findChild(stepNode.currentLang.mainFileName)
                } ?: return null
    }

    fun testFiles(project: Project, stepNode: StepNode): TestResult {
        val targetTestFile = prepareMainFile(project, stepNode) ?: return NO_PROCESS
        return testSamples(project, stepNode, "", { it.toBoolean() }, targetTestFile, true)
    }

    private fun prepareMainFile(project: Project, stepNode: StepNode): String? {
        val application = ApplicationManager.getApplication()
        val language = stepNode.currentLang

        val testFileName = language.testFileName

        val stepDirectory = project.baseDir.findFileByRelativePath(stepNode.path) ?: return null

        val testFile = stepDirectory.let {
            return@let it.findFileByRelativePath(listOf("tests", language.langName).joinToString("/"))
        }?.let {
                    return@let it.findChild(testFileName)
                }

        val mainFile = getMainFilePath(project, stepNode) ?: return null

        val targetFilePath = stepDirectory.let {
            val out = "out"
            return@let it.findChild(out) ?: createDirectory(application, it, out)
        }?.let {
                    val out = language.langName
                    return@let it.findChild(out) ?: createDirectory(application, it, out)
                }?.let {
                    return@let it.findChild(testFileName) ?: createFile(application, it, testFileName)
                } ?: return null

        val documentManager = FileDocumentManager.getInstance()
        val text = application.runReadAction(Computable<String> {
            val submissionText = documentManager.getDocument(mainFile)?.text ?: return@Computable null
            val submission = getTextUnderDirectives(submissionText, language)
            if (testFile != null) {
                val testText = documentManager.getDocument(testFile)?.text ?: return@Computable null
                return@Computable replaceCode(testText, submission, language)
            } else {
                return@Computable submission
            }
        }) ?: return null

        writeText(application, documentManager, targetFilePath, text)

        return targetFilePath.path
    }

    fun writeText(application: Application, documentManager: FileDocumentManager, targetFilePath: VirtualFile, text: String) {
        application.invokeAndWait {
            application.runWriteAction {
                val document = documentManager.getDocument(targetFilePath) ?: return@runWriteAction
                document.setText(text)
                documentManager.saveDocument(document)
            }
        }
    }

    fun createFile(application: Application, parent: VirtualFile, testFileName: String): VirtualFile? {
        var file: VirtualFile? = null
        application.invokeAndWait {
            file = application.runWriteAction(Computable<VirtualFile> {
                parent.findOrCreateChildData(null, testFileName)
            })
        }

        return file
    }
}
