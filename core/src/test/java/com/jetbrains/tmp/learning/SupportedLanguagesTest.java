package com.jetbrains.tmp.learning;

import com.intellij.openapi.util.Pair;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.jetbrains.tmp.learning.SupportedLanguages.ASM32;
import static com.jetbrains.tmp.learning.SupportedLanguages.ASM64;
import static com.jetbrains.tmp.learning.SupportedLanguages.C;
import static com.jetbrains.tmp.learning.SupportedLanguages.CLOJURE;
import static com.jetbrains.tmp.learning.SupportedLanguages.CPP;
import static com.jetbrains.tmp.learning.SupportedLanguages.CPP_11;
import static com.jetbrains.tmp.learning.SupportedLanguages.HASKELL;
import static com.jetbrains.tmp.learning.SupportedLanguages.HASKELL_7_10;
import static com.jetbrains.tmp.learning.SupportedLanguages.HASKELL_8_0;
import static com.jetbrains.tmp.learning.SupportedLanguages.JAVA;
import static com.jetbrains.tmp.learning.SupportedLanguages.JAVA8;
import static com.jetbrains.tmp.learning.SupportedLanguages.JAVASCRIPT;
import static com.jetbrains.tmp.learning.SupportedLanguages.MONO_CS;
import static com.jetbrains.tmp.learning.SupportedLanguages.OCTAVE;
import static com.jetbrains.tmp.learning.SupportedLanguages.PYTHON3;
import static com.jetbrains.tmp.learning.SupportedLanguages.R;
import static com.jetbrains.tmp.learning.SupportedLanguages.RUBY;
import static com.jetbrains.tmp.learning.SupportedLanguages.RUST;
import static com.jetbrains.tmp.learning.SupportedLanguages.SCALA;
import static com.jetbrains.tmp.learning.SupportedLanguages.SHELL;
import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
@RunWith(Theories.class)
public class SupportedLanguagesTest {
    @DataPoints("languages")
    public static final Pair[] languagesNames = new Pair[]{
            new Pair<>(PYTHON3, "python3"),
            new Pair<>(C, "c"),
            new Pair<>(CPP, "c++"),
            new Pair<>(CPP_11, "c++11"),
            new Pair<>(HASKELL, "haskell"),
            new Pair<>(HASKELL_7_10, "haskell 7.10"),
            new Pair<>(HASKELL_8_0, "haskell 8.0"),
            new Pair<>(JAVA, "java"),
            new Pair<>(JAVA8, "java8"),
            new Pair<>(OCTAVE, "octave"),
            new Pair<>(ASM32, "asm32"),
            new Pair<>(ASM64, "asm64"),
            new Pair<>(SHELL, "shell"),
            new Pair<>(RUST, "rust"),
            new Pair<>(R, "r"),
            new Pair<>(RUBY, "ruby"),
            new Pair<>(CLOJURE, "clojure"),
            new Pair<>(MONO_CS, "mono c#"),
            new Pair<>(JAVASCRIPT, "javascript"),
            new Pair<>(SCALA, "scala")
    };

    @Theory
    public void langOf(@FromDataPoints("languages") Pair<SupportedLanguages, String> language) throws Exception {
        assertEquals(language.getFirst(), SupportedLanguages.langOfName(language.getSecond()));
    }

}