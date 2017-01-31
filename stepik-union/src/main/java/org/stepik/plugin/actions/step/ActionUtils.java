package org.stepik.plugin.actions.step;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.submissions.Submission;

import java.util.Optional;

class ActionUtils {
    static final int MILLISECONDS_IN_MINUTES = 60 * 1000;
    private static final Logger logger = Logger.getInstance(ActionUtils.class);
    private static final int MILLISECONDS_IN_HOUR = 60 * MILLISECONDS_IN_MINUTES;
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";

    static boolean checkLangSettings(StepNode stepNode, Project project) {
        String srcPath = String.join("/", stepNode.getPath(), EduNames.SRC);
        VirtualFile src = project.getBaseDir().findFileByRelativePath(srcPath);
        if (src == null) {
            logger.warn("Can't find VF for: " + srcPath);
            return false;
        }

        Optional<SupportedLanguages> lang = Optional.of(stepNode.getCurrentLang());
        if (src.findChild(stepNode.getCurrentLang().getMainFileName()) == null) {
            lang = stepNode.getSupportedLanguages()
                    .stream()
                    .filter(x -> src.findChild(x.getMainFileName()) != null)
                    .findFirst();
        }

        if (!lang.isPresent()) {
            logger.warn("Lang settings is broken. Please create new project.");
            return false;
        }
        stepNode.setCurrentLang(lang.get());
        return true;
    }

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
