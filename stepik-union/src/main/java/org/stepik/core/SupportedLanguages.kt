package org.stepik.core

enum class SupportedLanguages constructor(val title: String,
                                          val langName: String,
                                          val mainFileName: String,
                                          val comment: String,
                                          val beforeCode: String?,
                                          val afterCode: String?,
                                          private val nextVersion: SupportedLanguages? = null) {
    ASM32("asm32", "asm32", "main32.asm", "#", null, null),
    ASM64("asm64", "asm64", "main64.asm", "#", null, null),
    C("C", "c", "main.c", "//", null, null),
    CLOJURE("Clojure", "clojure", "main.clj", ";;", null, null),
    CPP("C++", "c++", "main.cpp", "//", null, null),
    CPP_11("C++11", "c++11", "main_11.cpp", "//", null, null),
    GO("Go", "go", "main.go", "//", null, null),
    HASKELL("Haskell 7.8",
            "haskell",
            "Main.hs",
            "--",
            "module Main where\nmain = print $ \"Hello, world!\"", null),
    HASKELL_7_10("Haskell 7.10",
            "haskell 7.10",
            "Main.hs",
            "--",
            "module Main where\nmain = print $ \"Hello, world!\"", null),
    HASKELL_8_0("Haskell 8.0",
            "haskell 8.0",
            "Main.hs",
            "--",
            "module Main where\nmain = print $ \"Hello, world!\"", null),
    JAVA8("Java 8", "java8", "Main.java", "//", "class Main {", "}"),
    JAVA("Java", "java", "Main.java", "//", "class Main {", "}", JAVA8),
    JAVASCRIPT("JavaScript", "javascript", "main.js", "//", null, null),
    KOTLIN("Kotlin", "kotlin", "Main.kt", "//", null, null),
    MONO_CS("Mono c#", "mono c#", "main.cs", "//", null, null),
    OCTAVE("Octave", "octave", "main.m", "%", null, null),
    PASCAL_ABC("PascalABC.NET", "pascalabc", "Main.pas", "//", null, null),
    PYTHON3("Python 3", "python3", "main.py", "#", null, null),
    R("R", "r", "main.r", "#", null, null),
    RUBY("Ruby", "ruby", "main.rb", "#", null, null),
    RUST("Rust", "rust", "main.rs", "//", null, null),
    SCALA("Scala", "scala", "Main.scala", "//", null, null),
    SHELL("Shell", "shell", "main.sh", "#", null, null),
    INVALID("invalid", "invalid", "", "", null, null);

    /**
     * Checks whether the comment string

     * @param line String to check
     * *
     * @return true if line is commented, false otherwise
     */
    fun isCommentedLine(line: String) = line.trimStart { it <= ' ' }.startsWith(comment)

    override fun toString() = title

    fun upgradedTo(language: SupportedLanguages): Boolean {
        return language == this || this.nextVersion?.upgradedTo(language) ?: false
    }

    fun comment(string: String) = comment + string

    companion object {
        private val nameMap: Map<String, SupportedLanguages> by lazy {
            values().map { it.langName to it }.toMap()
        }

        private val titleMap: Map<String, SupportedLanguages> by lazy {
            values().map { it.title to it }.toMap()
        }

        fun langOfName(lang: String) = nameMap.getOrElse(lang, { INVALID })

        fun langOfTitle(lang: String) = titleMap.getOrElse(lang, { INVALID })
    }
}
