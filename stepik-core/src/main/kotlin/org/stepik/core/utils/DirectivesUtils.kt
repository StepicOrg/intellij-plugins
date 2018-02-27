package org.stepik.core.utils

import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.SupportedLanguages
import java.util.*

private const val START_DIRECTIVE = "Stepik code: start"
private const val END_DIRECTIVE = "Stepik code: end"
private const val START_HINT = "Please note, only the code BELOW will be sent to Stepik.org"
private const val END_HINT = "Please note, only the code ABOVE will be sent to Stepik.org"

private const val MESSAGE = "Do you want to remove Stepik directives and external code?\n" + "You can undo this action using \"ctrl + Z\"."

fun getFileText(vf: VirtualFile): String {
    return getApplication().runReadAction(Computable {
        val document = FileDocumentManager.getInstance().getDocument(vf)
        return@Computable document?.text ?: ""
    })
}

fun getTextUnderDirectives(text: String, language: SupportedLanguages): String {
    val result = text.split("\n").toMutableList()
    val (start, end) = findDirectives(result, language)

    val commentPrefix = language.comment

    for (i in 0 until start) {
        result[i] = commentPrefix + result[i]
    }
    for (i in end + 1 until result.size) {
        result[i] = commentPrefix + result[i]
    }

    return result.joinToString("\n")
}

/**
 * Find first "Stepik code: start" and last "Stepik code: end". Return Pair(start, end).
 *
 * If "Stepik code: start" not found, start = -1
 *
 * If "Stepik code: end" not found, end = text.length
 */

fun findDirectives(text: String, language: SupportedLanguages): Pair<Int, Int> {
    return findDirectives(text.split("\n"), language)
}

fun findDirectives(text: List<String>, language: SupportedLanguages): Pair<Int, Int> {
    var start = -1
    var end = text.size
    val commentPrefixSize = language.comment.length

    for (i in text.indices) {
        val line = text[i]
        if (language.isCommentedLine(line) && isStart(line, commentPrefixSize)) {
            start = i
            break
        }
    }

    for (i in text.size - 1 downTo start + 1) {
        val line = text[i]
        if (language.isCommentedLine(line) && isEnd(line, commentPrefixSize)) {
            end = i
            break
        }
    }

    return start to end
}

private fun isStart(line: String, commentPrefixSize: Int): Boolean {
    return START_DIRECTIVE == line.trim().substring(commentPrefixSize).trim()
}

private fun isEnd(line: String, commentPrefixSize: Int): Boolean {
    return END_DIRECTIVE == line.trim().substring(commentPrefixSize).trim()
}

fun writeTextToFile(text: String, file: VirtualFile, project: Project) {
    val document = FileDocumentManager.getInstance().getDocument(file) ?: return

    CommandProcessor.getInstance()
            .executeCommand(project, {
                getApplication().runWriteAction { document.setText(text) }
            }, "Stepik directives process", "Stepik directives process")
}

fun removeAmbientCode(text: String, showHint: Boolean, language: SupportedLanguages, confirm: Boolean = true): String {
    val splittedText = text.split("\n")
    val size = splittedText.size
    val (start, end) = findDirectives(text, language)

    val k = if (showHint) 1 else 0
    val before = if (start > 0) splittedText.subList(0, start - k).joinToString("\n") else ""
    val e = end + k + 1
    val after = if (e < size) splittedText.subList(e, size).joinToString("\n") else ""

    if (confirm && (before != language.beforeCode || after != language.afterCode)) {
        val answer = Messages.showYesNoDialog(MESSAGE, "Information", Messages.getInformationIcon())
        if (answer != Messages.YES) {
            return splittedText.joinToString("\n")
        }
    }

    return splittedText.subList(start + 1, end).joinToString("\n")
}


fun StringBuilder.appendLnIf(string: String, condition: Boolean): StringBuilder {
    return if (condition) {
        appendln(string)
    } else {
        this
    }
}

fun insertAmbientCode(text: String, lang: SupportedLanguages, showHint: Boolean): String {
    return StringBuilder().appendln(lang.beforeCode ?: "")
            .appendLnIf(lang.comment(START_HINT), showHint)
            .appendln(lang.comment(START_DIRECTIVE))
            .appendln(text)
            .appendln(lang.comment(END_DIRECTIVE))
            .appendLnIf(lang.comment(END_HINT), showHint)
            .append(lang.afterCode ?: "")
            .toString()
            .trim()
}

fun replaceCode(text: String, code: String, language: SupportedLanguages): String {
    val textLines = text.split("\n")
    val codeLines = code.split("\n")
    val (start, end) = findDirectives(textLines, language)
    val beforeCodeSize = start + 1
    val afterCodeSize = textLines.size - end

    val resultLines = mutableListOf<String>()
    resultLines.addAll(textLines.subList(0, beforeCodeSize))
    resultLines.addAll(codeLines)
    resultLines.addAll(textLines.subList(end, end + afterCodeSize))

    return resultLines.joinToString("\n")
}

fun uncommentAmbientCode(text: String, language: SupportedLanguages): String {
    var lines = text.split("\n")
    val (start, end) = findDirectives(lines, language)
    val commentPrefix = language.comment
    val commentPrefixSize = commentPrefix.length
    for (i in 0 until start) {
        lines = uncomment(language, lines, commentPrefixSize, i)
    }
    for (i in end + 1 until lines.size) {
        lines = uncomment(language, lines, commentPrefixSize, i)
    }

    return lines.joinToString("\n")
}

private fun uncomment(language: SupportedLanguages,
                      lines: List<String>,
                      commentPrefixSize: Int,
                      lineIndex: Int): List<String> {
    val result = ArrayList(lines)
    if (language.isCommentedLine(lines[lineIndex])) {
        result[lineIndex] = lines[lineIndex].substring(commentPrefixSize)
    }

    return result
}

fun containsDirectives(text: String, language: SupportedLanguages): Boolean {
    val splittedText = text.split("\n")
    val commentPrefixSize = language.comment.length

    for (i in splittedText.indices) {
        var line = splittedText[i]
        if (language.isCommentedLine(line)) {
            line = splittedText[i].trim().substring(commentPrefixSize).trim()
            if (START_DIRECTIVE == line || END_DIRECTIVE == line) {
                return true
            }
        }
    }

    return false
}
