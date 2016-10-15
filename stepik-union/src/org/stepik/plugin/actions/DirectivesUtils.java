package org.stepik.plugin.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.stepik.plugin.collective.SupportedLanguages;

import java.util.Arrays;

public class DirectivesUtils {
    private static final String START_DIRECTIVE = "Stepik code: start";
    private static final String END_DIRECTIVE = "Stepik code: end";

    private static final String MESSAGE = "Do you want to remove Stepik directives and external code?\n" +
            "You can undo this action using \"ctrl + Z\".";

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
        Integer start = -1;
        Integer end = text.length;
        for (int i = 0; i < text.length; i++) {
            String line = text[i].trim();
            if (line.startsWith(lang.getComment())) {
                line = line.substring(2).trim();
                if (start == -1 && isStart(line)) {
                    start = i;
                    continue;
                }

                if (isEnd(line)) end = i;
            }
        }
        return Pair.create(start, end);
    }

    private static boolean isStart(String line) {
        return START_DIRECTIVE.equals(line);
    }

    private static boolean isEnd(String line) {
        return END_DIRECTIVE.equals(line);
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
                                            Project project) {
        int start = locations.first;
        int end = locations.second;
        final int k = showHint ? 2 : 1;

        if (start > k || text.length - end > 1 + k) {
            int information = Messages.showYesNoDialog(project, MESSAGE, "Information", Messages.getInformationIcon());
            if (information != 0) return text;
        }

        return Arrays.copyOfRange(text, start + 1, end);
    }

    public static String[] insertMainClass(String[] text) {
        String[] ans = new String[text.length + 2];
        ans[0] = "class Main {";
        for (int i = 0; i < text.length; i++) {
            ans[i + 1] = text[i];
        }
        ans[ans.length - 1] = "}";

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