package org.stepik.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.stepik.core.SupportedLanguages.ASM32
import org.stepik.core.SupportedLanguages.ASM64
import org.stepik.core.SupportedLanguages.C
import org.stepik.core.SupportedLanguages.CLOJURE
import org.stepik.core.SupportedLanguages.CPP
import org.stepik.core.SupportedLanguages.CPP_11
import org.stepik.core.SupportedLanguages.GO
import org.stepik.core.SupportedLanguages.HASKELL
import org.stepik.core.SupportedLanguages.HASKELL_7_10
import org.stepik.core.SupportedLanguages.HASKELL_8_0
import org.stepik.core.SupportedLanguages.JAVA
import org.stepik.core.SupportedLanguages.JAVA8
import org.stepik.core.SupportedLanguages.JAVASCRIPT
import org.stepik.core.SupportedLanguages.KOTLIN
import org.stepik.core.SupportedLanguages.MONO_CS
import org.stepik.core.SupportedLanguages.OCTAVE
import org.stepik.core.SupportedLanguages.PASCAL_ABC
import org.stepik.core.SupportedLanguages.PYTHON3
import org.stepik.core.SupportedLanguages.R
import org.stepik.core.SupportedLanguages.RUBY
import org.stepik.core.SupportedLanguages.RUST
import org.stepik.core.SupportedLanguages.SCALA
import org.stepik.core.SupportedLanguages.SHELL


@RunWith(Theories::class)
class SupportedLanguagesTest {

    @Theory
    fun langOfName(@FromDataPoints("languagesToNames") language: Pair<SupportedLanguages, String>) {
        assertEquals(language.first, SupportedLanguages.langOfName(language.second))
    }

    @Theory
    fun langOfTitle(@FromDataPoints("languagesToTitles") language: Pair<SupportedLanguages, String>) {
        assertEquals(language.first, SupportedLanguages.langOfTitle(language.second))
    }

    @Test
    fun isCommentedLine() {
        assertTrue(JAVA8.isCommentedLine("   // Commented     "))
    }

    @Test
    fun isNotCommentedLine() {
        assertFalse(JAVA8.isCommentedLine("public static main(String... args) {"))
    }

    @Test
    fun upgradedTo() {
        assertTrue(JAVA.upgradedTo(JAVA))
        assertTrue(JAVA.upgradedTo(JAVA8))
    }

    @Test
    fun notUpgradedTo() {
        assertFalse(PYTHON3.upgradedTo(GO))
    }

    companion object {
        @DataPoints("languagesToNames")
        @JvmField
        val languagesToNames = listOf(
                ASM32 to "asm32",
                ASM64 to "asm64",
                C to "c",
                CLOJURE to "clojure",
                CPP to "c++",
                CPP_11 to "c++11",
                GO to "go",
                HASKELL to "haskell",
                HASKELL_7_10 to "haskell 7.10",
                HASKELL_8_0 to "haskell 8.0",
                JAVA to "java",
                JAVA8 to "java8",
                JAVASCRIPT to "javascript",
                KOTLIN to "kotlin",
                MONO_CS to "mono c#",
                OCTAVE to "octave",
                PASCAL_ABC to "pascalabc",
                PYTHON3 to "python3",
                R to "r",
                RUBY to "ruby",
                RUST to "rust",
                SCALA to "scala",
                SHELL to "shell")

        @DataPoints("languagesToTitles")
        @JvmField
        val languagesToTitles = listOf(
                ASM32 to "asm32",
                ASM64 to "asm64",
                C to "C",
                CLOJURE to "Clojure",
                CPP to "C++",
                CPP_11 to "C++11",
                GO to "Go",
                HASKELL to "Haskell 7.8",
                HASKELL_7_10 to "Haskell 7.10",
                HASKELL_8_0 to "Haskell 8.0",
                JAVA to "Java",
                JAVA8 to "Java 8",
                JAVASCRIPT to "JavaScript",
                KOTLIN to "Kotlin",
                MONO_CS to "Mono c#",
                OCTAVE to "Octave",
                PASCAL_ABC to "PascalABC.NET",
                PYTHON3 to "Python 3",
                R to "R",
                RUBY to "Ruby",
                RUST to "Rust",
                SCALA to "Scala",
                SHELL to "Shell")
    }
}
