package org.stepik.core.actions

import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.keymap.KeymapUtil
import org.stepik.api.objects.submissions.Submission
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow
import javax.swing.KeyStroke.getKeyStroke

const val MILLISECONDS_IN_MINUTES = 60 * 1000
private const val MILLISECONDS_IN_HOUR = 60 * MILLISECONDS_IN_MINUTES

fun etaAsString(eta: Long): String {
    var value = eta
    var result = ""

    val hours = value / MILLISECONDS_IN_HOUR
    if (hours > 0) {
        result += timePart(hours, "hour")
        value -= hours * MILLISECONDS_IN_HOUR
    }

    val minutes = value / MILLISECONDS_IN_MINUTES
    if (minutes > 0) {
        result += timePart(minutes, "minute")
        value -= minutes * MILLISECONDS_IN_MINUTES
    }

    val seconds = value / 1000
    if (seconds > 0) {
        result += timePart(seconds, "second")
    }

    return result.trim { it <= ' ' }
}

private fun timePart(value: Long, label: String): String {
    val suffix = if (value == 1L) "" else "s"
    return "$value $label$suffix "
}

fun setupCheckProgress(
        resultWindow: StepikTestResultToolWindow,
        submission: Submission,
        timer: Long) {
    val eta = submission.eta * 1000 //to ms

    resultWindow.apply {
        print("[${Math.round((1 - eta / (eta + timer)) * 100)}%] ")
        print("Ends in ${etaAsString(eta.toLong())}")
    }
}

fun getShortcutText(shortcut: String): String {
    val keyboardShortcut = KeyboardShortcut(getKeyStroke(shortcut), null)
    return KeymapUtil.getShortcutText(keyboardShortcut)
}
