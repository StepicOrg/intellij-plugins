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
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class DirectivesUtils {
    private static final String START_DIRECTIVE = "Stepik code: start";
    private static final String END_DIRECTIVE = "Stepik code: end";

    private static final String MESSAGE = "Do you want to remove Stepik directives and external code?\n" +
            "You can undo this action using \"ctrl + Z\".";

    private static final Map<SupportedLanguages, String[]> BEFORE_TEXT = new HashMap<>();
    static {
        BEFORE_TEXT.put(SupportedLanguages.JAVA, new String[]{"class Main {"});
        BEFORE_TEXT.put(SupportedLanguages.PYTHON, new String[]{"class Main:"});
    }
    private static final Map<SupportedLanguages, String[]> AFTER_TEXT = new HashMap<>();
    static {
        AFTER_TEXT.put(SupportedLanguages.JAVA, new String[]{"}"});
    }

    public static String[] getFileText(VirtualFile vf) {
        Document document = FileDocumentManager.getInstance().getDocument(vf);
        return document.getText().split("\n");
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
     * If "Stepik code: start" not found, start = -1
     * If "Stepik code: end" not found, end = text.length
     */
    public static Pair<Integer, Integer> findDirectives(String[] text, SupportedLanguages lang) {
        Integer start;
        Integer end;

        start = findDirective(text, 0, DirectivesUtils::isStart, lang);
        end = findDirective(text, start + 1, DirectivesUtils::isEnd, lang);

        return Pair.create(start, end == -1? text.length : end);
    }

    private static int findDirective(String[] lines, int start, BiPredicate<String, SupportedLanguages> finder, SupportedLanguages lang) {

        for (int i = start; i < lines.length; i++) {

            if (finder.test(lines[i], lang)) {
                return i;
            }
        }

        return -1;
    }

    private static boolean isStart(String line, SupportedLanguages lang) {

        line = line.trim();

        return line.startsWith(lang.getComment()) && START_DIRECTIVE.equals(line.substring(2).trim());
    }

    private static boolean isEnd(String line, SupportedLanguages lang) {

        line = line.trim();

        return line.startsWith(lang.getComment()) && END_DIRECTIVE.equals(line.substring(2).trim());
    }

    public static void writeInToFile(String[] text, VirtualFile file, Project project) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
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

    public static String[] removeDirectives(String[] text,
                                            Pair<Integer, Integer> locations,
                                            boolean showHint,
                                            Project project,
                                            @NotNull SupportedLanguages lang) {
        int start = locations.first;
        int end = locations.second;
        final int k = showHint ? 2 : 1;

        String[] before = Arrays.copyOf(text, start);
        String[] after = Arrays.copyOfRange(text, end + 1, text.length);

        //TODO Will store text and will not ask
        if (!Arrays.equals(before, BEFORE_TEXT.get(lang)) ||
                !Arrays.equals(after, AFTER_TEXT.get(lang))) {
            int information = Messages.showYesNoDialog(project, MESSAGE, "Information", Messages.getInformationIcon());
            if (information != 0) return text;
        }

        String[] result = new String[end - (start + 1)];

        for (int i = 0; i < result.length; i++) {
            result[i] = text[i + start + 1].replaceFirst("^\\t", "");
        }

        return result;
    }

    public static String[] insertMainClass(@NotNull String[] text, @NotNull SupportedLanguages lang) {
        //TODO To get text from storage if text was stored
        String[] before = BEFORE_TEXT.getOrDefault(lang, new String[0]);
        String[] after = AFTER_TEXT.getOrDefault(lang, new String[0]);

        String[] ans = new String[text.length + before.length + after.length];

        if (before.length != 0) {
            for (int i = 0; i < before.length; i++) {
                ans[i] = before[i];
            }
        }

        for (int i = 0; i < text.length; i++) {
            ans[i + before.length] = "\t" + text[i];
        }

        if (after.length != 0) {

            int start = text.length + before.length;

            for (int i = 0; i < after.length; i++) {
                ans[i + start] = after[i];
            }
        }

        return ans;
    }

    public static String[] insertDirectives(String[] text, SupportedLanguages lang, boolean showHint) {

        int k = showHint ? 2 : 1;
        String[] ans = new String[text.length + 2 * k];
        if (showHint) {
            ans[0] = lang.getComment() + "Please note, only the code below will be sent to Stepik.org";
            ans[1] = lang.getComment() + "Stepik code: start";
            ans[ans.length - 2] = lang.getComment() + "Stepik code: end";
            ans[ans.length - 1] = lang.getComment() + "Please note, only the code above will be sent to Stepik.org";
        } else {
            ans[0] = lang.getComment() + "Stepik code: start";
            ans[ans.length - 1] = lang.getComment() + "Stepik code: end";
        }

        for (int i = 0; i < text.length; i++) {
            ans[i + k] = text[i];
        }

        return ans;
    }
}