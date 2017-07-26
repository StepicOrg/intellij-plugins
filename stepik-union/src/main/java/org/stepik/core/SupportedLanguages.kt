package org.stepik.core

import org.stepik.core.testFramework.runners.JavaRunner
import org.stepik.core.testFramework.runners.KotlinRunner
import org.stepik.core.testFramework.runners.PythonRunner
import org.stepik.core.testFramework.runners.Runner
import org.stepik.core.testFramework.runners.StubRunner

enum class SupportedLanguages constructor(val title: String,
                                          val langName: String,
                                          val mainFileName: String,
                                          val testFileName: String,
                                          val comment: String,
                                          val beforeCode: String? = null,
                                          val afterCode: String? = null,
                                          private val nextVersion: SupportedLanguages? = null,
                                          val runner: Runner = StubRunner.instance) {
    ASM32("asm32", "asm32", "main32.asm", "test32.asm", "#"),
    ASM64("asm64", "asm64", "main64.asm", "test64.asm", "#"),
    C("C", "c", "main.c", "test.c", "//"),
    CLOJURE("Clojure", "clojure", "main.clj", "test.clj", ";;"),
    CPP("C++", "c++", "main.cpp", "test.cpp", "//"),
    CPP_11("C++11", "c++11", "main_11.cpp", "test_11.cpp", "//"),
    GO("Go", "go", "main.go", "test.go", "//"),
    HASKELL("Haskell 7.8",
            "haskell",
            "Main.hs",
            "Test_7_8.hs",
            "--",
            "module Main where\nmain = print $ \"Hello, world!\""),
    HASKELL_7_10("Haskell 7.10",
            "haskell 7.10",
            "Main.hs",
            "Test_7_10.hs",
            "--",
            "module Main where\nmain = print $ \"Hello, world!\""),
    HASKELL_8_0("Haskell 8.0",
            "haskell 8.0",
            "Main.hs",
            "Test_8_0.hs",
            "--",
            "module Main where\nmain = print $ \"Hello, world!\""),
    JAVA8("Java 8", "java8", "Main.java", "Test.java", "//", "class Main {", "}", runner = JavaRunner()),
    JAVA("Java", "java", "Main.java", "Test.java", "//", "class Main {", "}", JAVA8, runner = JavaRunner()),
    JAVASCRIPT("JavaScript", "javascript", "main.js", "test.js", "//"),
    KOTLIN("Kotlin", "kotlin", "Main.kt", "Test.kt", "//", runner = KotlinRunner()),
    MONO_CS("Mono c#", "mono c#", "main.cs", "test.cs", "//"),
    OCTAVE("Octave", "octave", "main.m", "test.m", "%"),
    PASCAL_ABC("PascalABC.NET", "pascalabc", "Main.pas", "Test.pas", "//"),
    PYTHON3("Python 3", "python3", "main.py", "test.py", "#", runner = PythonRunner()),
    R("R", "r", "main.r", "test.r", "#"),
    RUBY("Ruby", "ruby", "main.rb", "test.rb", "#"),
    RUST("Rust", "rust", "main.rs", "test.rs", "//"),
    SCALA("Scala", "scala", "Main.scala", "test.scala", "//"),
    SHELL("Shell", "shell", "main.sh", "test.sh", "#"),
    INVALID("invalid", "invalid", "", "", "");

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
