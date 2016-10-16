package org.stepik.plugin.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.collective.SupportedLanguages;

import java.util.Arrays;

public class DirectivesUtils {
    private static final String START_DIRECTIVE = "Stepik code: start";
    private static final String END_DIRECTIVE = "Stepik code: end";
    private static final String START_HINT = "Please note, only the code below will be sent to Stepik.org";
    private static final String END_HINT = "Please note, only the code above will be sent to Stepik.org";

    private static final String MESSAGE = "Do you want to remove Stepik directives and external code?\n" +
            "You can undo this action using \"ctrl + Z\".";

    public static String[] getFileText(VirtualFile vf) {
        Document document = FileDocumentManager.getInstance().getDocument(vf);
        return document != null ? document.getText().split("\n") : new String[0];
    }

    public static String getTextUnderDirectives(String[] text, SupportedLanguages lang) {
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
    public static Pair<Integer, Integer> findDirectives(String[] text, SupportedLanguages lang) {
        int start = -1;
        int end = text.length;

        for (int i = 0; i < text.length; i++) {
            if (isStart(text[i], lang)) {
                start = i;
                break;
            }
        }

        for (int i = start + 1; i < text.length; i++) {
            if (isEnd(text[i], lang)) {
                end = i;
                break;
            }
        }

        return Pair.create(start, end);
    }

    private static boolean isStart(String line, SupportedLanguages lang) {
        if (!lang.isCommentedLine(line))
            return false;

        line = line.trim().substring(lang.getComment().length()).trim();

        return START_DIRECTIVE.equals(line);
    }

    private static boolean isEnd(String line, SupportedLanguages lang) {
        if (!lang.isCommentedLine(line))
            return false;

        line = line.trim().substring(lang.getComment().length()).trim();

        return END_DIRECTIVE.equals(line);
    }

    public static void writeInToFile(String[] text, VirtualFile file, Project project) {
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

    public static String[] removeAmbientCode(String[] text,
                                             Pair<Integer, Integer> locations,
                                             Project project,
                                             @NotNull SupportedLanguages lang) {
        int start = locations.first;
        int end = locations.second;

        String[] before = Arrays.copyOfRange(text, 0, start > 0? start : 0);
        String[] after = Arrays.copyOfRange(text, end + 1, text.length);

        if (!Arrays.equals(before, lang.getBeforeCode()) ||
                !Arrays.equals(after, lang.getAfterCode())) {
            int information = Messages.showYesNoDialog(project, MESSAGE, "Information", Messages.getInformationIcon());
            if (information != 0) return text;
        }

        String[] result = new String[end - (start + 1)];

        for (int i = 0; i < result.length; i++) {
            result[i] = text[i + start + 1];
        }

        return result;
    }

    public static String[] insertAmbientCode(@NotNull String[] text, @NotNull SupportedLanguages lang, boolean showHint) {
        String[] beforeCode = lang.getBeforeCode();
        String[] afterCode = lang.getAfterCode();

        int k = showHint ? 2 : 1;

        String[] ans = new String[text.length + beforeCode.length + afterCode.length + 2 * k];
        System.arraycopy(beforeCode, 0, ans, 0, beforeCode.length);

        if (showHint) {
            ans[beforeCode.length] = START_HINT;
            ans[beforeCode.length + 1] = START_DIRECTIVE;
            ans[beforeCode.length + text.length + 1] = END_DIRECTIVE;
            ans[beforeCode.length + text.length + 2] = END_HINT;
        } else {
            ans[beforeCode.length] = START_DIRECTIVE;
            ans[beforeCode.length + text.length] = END_DIRECTIVE;
        }
        System.arraycopy(text, 0, ans, beforeCode.length + k, text.length);
        System.arraycopy(afterCode, 0, ans, beforeCode.length + text.length + 2 * k, afterCode.length);

        return ans;
    }
}