package org.stepik.plugin.actions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.stepik.plugin.collective.SupportedLanguages;

public class DirectivesUtils {

    public static String getTextUnderDirectives(VirtualFile vf, SupportedLanguages lang){
        Pair<Integer, Integer> locations = findDirectives(vf, lang);

        Document document = FileDocumentManager.getInstance().getDocument(vf);
        String text[] = document.getText().split("\n");

        int start = locations.first == null ? 0 : locations.first;
        int end = locations.second == null ? text.length : locations.second;

        StringBuilder sb = new StringBuilder();
        for (int i = start + 1; i < end; i++) {
            sb.append(text[i]).append("\n");
        }

        return sb.toString();
    }

    //    Find first "start" and last "end". If it was not find, method return null;
    public static Pair<Integer, Integer> findDirectives(VirtualFile vf, SupportedLanguages lang) {
        Document document = FileDocumentManager.getInstance().getDocument(vf);
        String[] text = document.getText().split("\n");
        Integer start = null;
        Integer end = null;
        for (int i = 0; i < text.length; i++) {
            String line = text[i].trim();
            if (line.startsWith(lang.getComment())) {
                line = line.substring(2).trim();
                if (start == null && isStart(line)) {
                    start = i;
                    continue;
                }

                if (isEnd(line)) end = i;
            }
        }
        return Pair.create(start, end);
    }

    private static boolean isStart(String line) {
        return line.equals("Stepik code: start");
    }

    private static boolean isEnd(String line) {
        return line.equals("Stepik code: end");
    }

    public static SupportedLanguages loadLangSettings(String lang) {
        lang = lang.replaceAll("[0-9]+","").toUpperCase();
        return SupportedLanguages.valueOf(lang);
    }
}