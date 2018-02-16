package org.stepik.core.actions

import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.keymap.KeymapUtil
import org.stepik.api.objects.submissions.Submission
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow
import javax.swing.KeyStroke

const val MILLISECONDS_IN_MINUTES = 60 * 1000
private const val MILLISECONDS_IN_HOUR = 60 * MILLISECONDS_IN_MINUTES
private const val HOUR = "hour"
private const val MINUTE = "minute"
private const val SECOND = "second"

fun etaAsString(eta: Long): String {
    var value = eta
    val result = StringBuilder()

    val hours = value / MILLISECONDS_IN_HOUR
    if (hours > 0) {
        appendTimePart(result, hours, HOUR)
        value -= hours * MILLISECONDS_IN_HOUR
    }

    val minutes = value / MILLISECONDS_IN_MINUTES
    if (minutes > 0) {
        appendTimePart(result, minutes, MINUTE)
        value -= minutes * MILLISECONDS_IN_MINUTES
    }

    val seconds = value / 1000
    if (seconds > 0) {
        appendTimePart(result, seconds, SECOND)
    }

    return result.toString().trim { it <= ' ' }
}

private fun appendTimePart(result: StringBuilder, value: Long, label: String) {
    result.append(value)
            .append(" ")
            .append(label)
            .append(if (value == 1L) "" else "s")
            .append(" ")
}

fun setupCheckProgress(
        resultWindow: StepikTestResultToolWindow,
        submission: Submission,
        timer: Long) {
    val eta = submission.eta * 1000 //to ms
    val total = eta + timer

    resultWindow.print("[${Math.round((1 - eta / total) * 100)}%] ")
    resultWindow.print("Ends in ${etaAsString(eta.toLong())}")
}

fun getShortcutText(shortcut: String): String {
    val keyboardShortcut = KeyboardShortcut(KeyStroke.getKeyStroke(shortcut), null)
    return KeymapUtil.getShortcutText(keyboardShortcut)
}
