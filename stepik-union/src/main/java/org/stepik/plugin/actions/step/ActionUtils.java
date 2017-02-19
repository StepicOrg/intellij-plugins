package org.stepik.plugin.actions.step;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.submissions.Submission;

class ActionUtils {
    static final int MILLISECONDS_IN_MINUTES = 60 * 1000;
    private static final int MILLISECONDS_IN_HOUR = 60 * MILLISECONDS_IN_MINUTES;
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";

    static String etaAsString(long eta) {
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

    static void notify(
            @NotNull Project project,
            @NotNull String title,
            @NotNull String content,
            @NotNull NotificationType type) {
        Notification notification = new Notification(
                "Step.sending",
                title,
                content,
                type);
        notification.notify(project);
    }

    static void notifyError(
            @NotNull Project project,
            @NotNull String title,
            @NotNull String content) {
        notify(project, title, content, NotificationType.ERROR);
    }

    static void setupCheckProgress(
            @NotNull ProgressIndicator indicator,
            @NotNull Submission submission,
            int timer) {
        double eta = submission.getEta() * 1000; //to ms
        double total = eta + timer;

        indicator.setFraction(1 - eta / total);
        indicator.setText2("Ends in " + etaAsString((long) eta));
    }
}
