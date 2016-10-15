package org.stepik.plugin.collective;

import org.jetbrains.annotations.NotNull;

public enum SupportedLanguages {
    JAVA("java8", "// ", "Main.java", new String[]{"class Maim {"}, new String[]{"}"}),
    PYTHON("python3", "# ", "main.py", new String[]{"class Main:"}, new String[0]);

    private final String name;
    private final String comment;
    private final String mainFileName;
    private final String[] beforeCode;
    private final String[] afterCode;

    /**
     * A Constructor for a supported language.
     *
     * @param name         Name language
     * @param comment      A string starting line comment
     * @param mainFileName A name for main file
     * @param beforeCode   Part of the surrounding code before text
     * @param afterCode    Part of the surrounding code after text
     */
    SupportedLanguages(@NotNull String name, @NotNull String comment, @NotNull String mainFileName,
                       @NotNull String[] beforeCode, @NotNull String[] afterCode) {
        this.name = name;
        this.comment = comment;
        this.mainFileName = mainFileName;
        this.beforeCode = beforeCode;
        this.afterCode = afterCode;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getMainFileName() {
        return mainFileName;
    }

    public static SupportedLanguages loadLangSettings(String lang) {
        lang = lang.replaceAll("[0-9]+", "").toUpperCase();
        return SupportedLanguages.valueOf(lang);
    }

    public String[] getBeforeCode() {
        return beforeCode;
    }

    public String[] getAfterCode() {
        return afterCode;
    }

    /**
     * Checks whether the comment string
     *
     * @param line String to check
     * @return true if line is commented, false otherwise
     */
    public boolean isCommentedLine(String line) {
        return line.trim().startsWith(comment);
    }
}