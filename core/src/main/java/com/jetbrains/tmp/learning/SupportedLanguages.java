package com.jetbrains.tmp.learning;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SupportedLanguages {
    ASM32("asm32", "main32.asm", "#", null, null),
    ASM64("asm64", "main64.asm", "#", null, null),
    C("C", "main.c", "//", null, null),
    CLOJURE("Clojure", "main.clj", ";;", null, null),
    CPP("C++11", "main.cpp", "//", null, null),
    HASKELL("Haskell 8.0", "Main.hs", "--", null, null),
    JAVA("Java 8", "Main.java", "//", new String[]{"class Main {"}, new String[]{"}"}),
    JAVASCRIPT("JavaScript", "main.js", "//", null, null),
    MONO_CS("Mono c#", "main.cs", "//", null, null),
    OCTAVE("Octave", "main.m", "%", null, null),
    PYTHON("Python 3", "main.py", "#", null, null),
    R("R", "main.r", "#", null, null),
    RUBY("Ruby", "main.rb", "#", null, null),
    RUST("Rust", "main.rs", "//", null, null),
    SHELL("Shell", "main.sh", "#", null, null),
    SCALA("Scala", "Main.scala", "//", null, null),
    INVALID("invalid", "", "", null, null);

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
    public static SupportedLanguages langOf(@NotNull String lang) {
        switch (lang) {
            case "java8":
                return JAVA;
            case "python3":
                return PYTHON;
            case "haskell 7.10":
            case "haskell 8.0":
                return HASKELL;
            case "mono c#":
                return MONO_CS;
            case "c++11":
            case "c++":
                return CPP;
        }
        lang = lang.toUpperCase();
        try {
            return SupportedLanguages.valueOf(lang);
        } catch (IllegalArgumentException e) {
            return INVALID;
        }
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
        return name;
    }
}