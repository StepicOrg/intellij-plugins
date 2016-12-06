package com.jetbrains.tmp.learning;

import com.jetbrains.tmp.learning.core.EduNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SupportedLanguages {
    JAVA(EduNames.JAVA8, "Main.java", "//", new String[]{"class Main {"}, new String[]{"}"}),
    PYTHON(EduNames.PYTHON3, "main.py", "#", null, null),
    INVALID("", "", "", null, null);

    private final String name;
    private final String comment;
    private final String mainFileName;
    private final String[] beforeCode;
    private final String[] afterCode;

    /**
     * A Constructor for a supported language.
     *
     * @param name         Language name
     * @param mainFileName A name for main file without extension
     * @param beforeCode   Part of the surrounding code before text
     * @param afterCode    Part of the surrounding code after text
     */
    SupportedLanguages(
            @NotNull String name, @NotNull String mainFileName, @NotNull String comment,
            @Nullable String[] beforeCode, @Nullable String[] afterCode) {
        this.name = name;
        this.mainFileName = mainFileName;
        this.comment = comment;
        this.beforeCode = beforeCode;
        this.afterCode = afterCode;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getComment() {
        return comment;
    }

    @NotNull
    public String getMainFileName() {
        return mainFileName;
    }

    @Nullable
    public static SupportedLanguages langOf(@NotNull String lang) {
        lang = lang.replaceAll("[0-9]+", "").toUpperCase();
        return SupportedLanguages.valueOf(lang);
    }

    @NotNull
    public String[] getBeforeCode() {
        return beforeCode != null ? beforeCode : new String[0];
    }

    @NotNull
    public String[] getAfterCode() {
        return afterCode != null ? afterCode : new String[0];
    }

    /**
     * Checks whether the comment string
     *
     * @param line String to check
     * @return true if line is commented, false otherwise
     */
    public boolean isCommentedLine(@NotNull String line) {
        return line.trim().startsWith(comment);
    }

    @Override
    public String toString() {
        return name;
    }

    public static SupportedLanguages type(String token) {
        return SupportedLanguages.langOf(token);
    }

    public static String token(SupportedLanguages t) {
        return t.name();
    }
}