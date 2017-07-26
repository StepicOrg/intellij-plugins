package org.stepik.plugin.actions;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow;

import javax.swing.*;

public class ActionUtils {
    static final int MILLISECONDS_IN_MINUTES = 60 * 1000;
    private static final int MILLISECONDS_IN_HOUR = 60 * MILLISECONDS_IN_MINUTES;
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";

    public static String etaAsString(long eta) {
        StringBuilder result = new StringBuilder();

        long hours = eta / MILLISECONDS_IN_HOUR;
        if (hours > 0) {
            appendTimePart(result, hours, HOUR);
            eta = eta - hours * MILLISECONDS_IN_HOUR;
        }

        long minutes = eta / MILLISECONDS_IN_MINUTES;
        if (minutes > 0) {
            appendTimePart(result, minutes, MINUTE);
            eta = eta - minutes * MILLISECONDS_IN_MINUTES;
        }

        long seconds = eta / 1000;
        if (seconds > 0) {
            appendTimePart(result, seconds, SECOND);
        }

        return result.toString().trim();
    }

    private static void appendTimePart(StringBuilder result, long value, String label) {
        result.append(value)
                .append(" ")
                .append(label)
                .append(value == 1 ? "" : "s")
                .append(" ");
    }

    static void setupCheckProgress(
            @NotNull StepikTestResultToolWindow resultWindow,
            @NotNull Submission submission,
            long timer) {
        double eta = submission.getEta() * 1000; //to ms
        double total = eta + timer;

        resultWindow.print("[" + Math.round((1 - eta / total) * 100) + "%] ", ConsoleViewContentType.NORMAL_OUTPUT);
        resultWindow.print("Ends in " + etaAsString((long) eta), ConsoleViewContentType.NORMAL_OUTPUT);
    }

    @NotNull
    public static String getShortcutText(@NotNull String shortcut) {
        KeyboardShortcut keyboardShortcut = new KeyboardShortcut(KeyStroke.getKeyStroke(shortcut), null);
        return KeymapUtil.getShortcutText(keyboardShortcut);
    }
}
