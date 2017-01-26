package org.stepik.plugin.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.SupportedLanguages;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DirectivesUtils {
    private static final String START_DIRECTIVE = "Stepik code: start";
    private static final String END_DIRECTIVE = "Stepik code: end";
    private static final String START_HINT = "Please note, only the code below will be sent to Stepik.org";
    private static final String END_HINT = "Please note, only the code above will be sent to Stepik.org";

    private static final String MESSAGE = "Do you want to remove Stepik directives and external code?\n" +
            "You can undo this action using \"ctrl + Z\".";

    @NotNull
    public static String[] getFileText(@NotNull VirtualFile vf) {
        return ApplicationManager.getApplication().runReadAction((Computable<String[]>) () -> {
            Document document = FileDocumentManager.getInstance().getDocument(vf);
            return document != null ? document.getText().split("\n") : new String[0];
        });
    }

    @NotNull
    public static String getTextUnderDirectives(@NotNull String[] text, @NotNull SupportedLanguages lang) {
        Pair<Integer, Integer> locations = findDirectives(text, lang);

        int start = locations.first;
        int end = locations.second;

        StringBuilder sb = new StringBuilder();

        for (int i = start + 1; i < end; i++) {
            sb.append(text[i]).append("\n");
        }

        return sb.toString();
    }

    /**
     * Find first "Stepik code: start" and last "Stepik code: end". Return Pair(start, end).
     * <p>
     * If "Stepik code: start" not found, start = -1
     * <p>
     * If "Stepik code: end" not found, end = text.length
     */

    @NotNull
    public static Pair<Integer, Integer> findDirectives(
            @NotNull String[] text,
            @NotNull SupportedLanguages lang) {
        int start = -1;
        int end = text.length;

        for (int i = 0; i < text.length; i++) {
            if (isStart(text[i], lang)) {
                start = i;
                break;
            }
        }

        for (int i = text.length - 1; i > start; i--) {
            if (isEnd(text[i], lang)) {
                end = i;
                break;
            }
        }

        return Pair.create(start, end);
    }

    private static boolean isStart(@NotNull String line, @NotNull SupportedLanguages lang) {
        if (!lang.isCommentedLine(line))
            return false;

        line = line.trim().substring(lang.getComment().length()).trim();

        return START_DIRECTIVE.equals(line);
    }

    private static boolean isEnd(@NotNull String line, @NotNull SupportedLanguages lang) {
        if (!lang.isCommentedLine(line))
            return false;

        line = line.trim().substring(lang.getComment().length()).trim();

        return END_DIRECTIVE.equals(line);
    }

    public static void writeInToFile(
            @NotNull String[] text,
            @NotNull VirtualFile file,
            @NotNull Project project) {
        final Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null)
            return;
        final StringBuilder sb = new StringBuilder();
        for (String tmp : text)
            sb.append(tmp).append("\n");

        CommandProcessor
                .getInstance()
                .executeCommand(project,
                        () -> ApplicationManager
                                .getApplication()
                                .runWriteAction(
                                        () -> document.setText(sb.toString())),
                        "Stepik directives process",
                        "Stepik directives process");
    }

    public static String[] removeAmbientCode(
            @NotNull String[] text,
            @NotNull Pair<Integer, Integer> locations,
            @NotNull Project project,
            boolean showHint,
            @NotNull SupportedLanguages lang) {
        int start = locations.first;
        int end = locations.second;

        int k = showHint ? 1 : 0;
        String[] before = start > 0 ? Arrays.copyOfRange(text, 0, start - k) : new String[0];
        int e = end + k + 1;
        String[] after = e < text.length ? Arrays.copyOfRange(text, e, text.length) : new String[0];

        if (!Arrays.equals(before, lang.getBeforeCode()) ||
                !Arrays.equals(after, lang.getAfterCode())) {
            int information = Messages.showYesNoDialog(project, MESSAGE, "Information",
                    Messages.getInformationIcon());
            if (information != 0) return text;
        }

        return Arrays.copyOfRange(text, start + 1, end);
    }

    @NotNull
    public static String[] insertAmbientCode(
            @NotNull String[] text, @NotNull SupportedLanguages lang,
            boolean showHint) {
        String[] beforeCode = lang.getBeforeCode();
        String[] afterCode = lang.getAfterCode();

        int k = showHint ? 2 : 1;

        String[] ans = new String[text.length + beforeCode.length + afterCode.length + 2 * k];
        System.arraycopy(beforeCode, 0, ans, 0, beforeCode.length);

        if (showHint) {
            ans[beforeCode.length] = lang.getComment() + START_HINT;
            ans[beforeCode.length + 1] = lang.getComment() + START_DIRECTIVE;
            ans[beforeCode.length + text.length + k] = lang.getComment() + END_DIRECTIVE;
            ans[beforeCode.length + text.length + k + 1] = lang.getComment() + END_HINT;
        } else {
            ans[beforeCode.length] = lang.getComment() + START_DIRECTIVE;
            ans[beforeCode.length + text.length + k] = lang.getComment() + END_DIRECTIVE;
        }
        System.arraycopy(text, 0, ans, beforeCode.length + k, text.length);
        System.arraycopy(afterCode, 0, ans, beforeCode.length + text.length + 2 * k, afterCode.length);

        return ans;
    }
}