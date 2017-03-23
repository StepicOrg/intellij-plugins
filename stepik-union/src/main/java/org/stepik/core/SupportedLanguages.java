package org.stepik.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum SupportedLanguages {
    ASM32("asm32", "asm32", "main32.asm", "#", null, null),
    ASM64("asm64", "asm64", "main64.asm", "#", null, null),
    C("C", "c", "main.c", "//", null, null),
    CLOJURE("Clojure", "clojure", "main.clj", ";;", null, null),
    CPP("C++", "c++", "main.cpp", "//", null, null),
    CPP_11("C++11", "c++11", "main_11.cpp", "//", null, null),
    HASKELL("Haskell 7.8",
            "haskell",
            "Main.hs",
            "--",
            new String[]{"module Main where", "main = print $ \"Hello world!\""},
            null),
    HASKELL_7_10("Haskell 7.10",
            "haskell 7.10",
            "Main.hs",
            "--",
            new String[]{"module Main where", "main = print $ \"Hello world!\""},
            null),
    HASKELL_8_0("Haskell 8.0",
            "haskell 8.0",
            "Main.hs",
            "--",
            new String[]{"module Main where", "main = print $ \"Hello world!\""},
            null),
    JAVA8("Java 8", "java8", "Main.java", "//", new String[]{"class Main {"}, new String[]{"}"}),
    JAVA("Java", "java", "Main.java", "//", new String[]{"class Main {"}, new String[]{"}"}, JAVA8),
    JAVASCRIPT("JavaScript", "javascript", "main.js", "//", null, null),
    MONO_CS("Mono c#", "mono c#", "main.cs", "//", null, null),
    OCTAVE("Octave", "octave", "main.m", "%", null, null),
    PYTHON3("Python 3", "python3", "main.py", "#", null, null),
    R("R", "r", "main.r", "#", null, null),
    RUBY("Ruby", "ruby", "main.rb", "#", null, null),
    RUST("Rust", "rust", "main.rs", "//", null, null),
    SHELL("Shell", "shell", "main.sh", "#", null, null),
    SCALA("Scala", "scala", "Main.scala", "//", null, null),
    INVALID("invalid", "invalid", "", "", null, null);

    private static Map<String, SupportedLanguages> nameMap;
    private static Map<String, SupportedLanguages> titleMap;
    private final String name;
    private final String comment;
    private final String mainFileName;
    private final String[] beforeCode;
    private final String[] afterCode;
    private final String title;
    private final SupportedLanguages nextVersion;

    /**
     * A Constructor for a supported language.
     *
     * @param name         Language name
     * @param mainFileName A name for main file without extension
     * @param beforeCode   Part of the surrounding code before text
     * @param afterCode    Part of the surrounding code after text
     */
    SupportedLanguages(
            @NotNull String title,
            @NotNull String name,
            @NotNull String mainFileName,
            @NotNull String comment,
            @Nullable String[] beforeCode,
            @Nullable String[] afterCode) {
        this(title, name, mainFileName, comment, beforeCode, afterCode, null);

    }

    SupportedLanguages(
            @NotNull String title,
            @NotNull String name,
            @NotNull String mainFileName,
            @NotNull String comment,
            @Nullable String[] beforeCode,
            @Nullable String[] afterCode,
            @Nullable SupportedLanguages nextVersion) {
        this.title = title;
        this.name = name;
        this.mainFileName = mainFileName;
        this.comment = comment;
        this.beforeCode = beforeCode;
        this.afterCode = afterCode;
        this.nextVersion = nextVersion;
    }

    @NotNull
    public static SupportedLanguages langOfName(@NotNull String lang) {
        if (nameMap == null) {
            nameMap = new HashMap<>();
            Arrays.stream(values()).forEach(value -> nameMap.put(value.getName(), value));
        }

        return nameMap.getOrDefault(lang, INVALID);
    }

    @NotNull
    public static SupportedLanguages langOfTitle(@NotNull String lang) {
        if (titleMap == null) {
            titleMap = new HashMap<>();
            Arrays.stream(values()).forEach(value -> titleMap.put(value.getTitle(), value));
        }

        return titleMap.getOrDefault(lang, INVALID);
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

    @NotNull
    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public boolean upgradedTo(@NotNull SupportedLanguages languages) {
        return languages == this || (this.nextVersion != null && this.nextVersion.upgradedTo(languages));
    }
}
