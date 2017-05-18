package org.stepik.core;

import com.intellij.openapi.util.Pair;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.stepik.core.SupportedLanguages.ASM32;
import static org.stepik.core.SupportedLanguages.ASM64;
import static org.stepik.core.SupportedLanguages.C;
import static org.stepik.core.SupportedLanguages.CLOJURE;
import static org.stepik.core.SupportedLanguages.CPP;
import static org.stepik.core.SupportedLanguages.CPP_11;
import static org.stepik.core.SupportedLanguages.HASKELL;
import static org.stepik.core.SupportedLanguages.HASKELL_7_10;
import static org.stepik.core.SupportedLanguages.HASKELL_8_0;
import static org.stepik.core.SupportedLanguages.JAVA;
import static org.stepik.core.SupportedLanguages.JAVA8;
import static org.stepik.core.SupportedLanguages.JAVASCRIPT;
import static org.stepik.core.SupportedLanguages.KOTLIN;
import static org.stepik.core.SupportedLanguages.MONO_CS;
import static org.stepik.core.SupportedLanguages.OCTAVE;
import static org.stepik.core.SupportedLanguages.PYTHON3;
import static org.stepik.core.SupportedLanguages.R;
import static org.stepik.core.SupportedLanguages.RUBY;
import static org.stepik.core.SupportedLanguages.RUST;
import static org.stepik.core.SupportedLanguages.SCALA;
import static org.stepik.core.SupportedLanguages.SHELL;

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
            new Pair<>(KOTLIN, "kotlin"),
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