package org.stepik.plugin.projectWizard

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
import org.stepik.core.SupportedLanguages.JAVA7
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

object CoursesList {
    private val DEFAULT_COURSES = listOf(156L, 217L, 1127L, 125L, 126L, 150L)

    private val ASM_COURSES = listOf(253L, 1780L, 156L, 217L, 1127L, 125L, 126L, 150L)
    private val CPP_COURSES = listOf(579L, 363L, 144L, 153L, 538L, 7L, 156L, 217L, 1127L, 125L, 126L, 150L)
    private val HASKELL_COURSES = listOf(75L, 693L, 156L, 217L, 1127L, 125L, 126L, 150L)
    private val JAVA_COURSES = listOf(187L, 150L, 2403L, 2600L, 1891L, 1595L, 2262L, 217L, 1127L, 125L, 126L)
    private val JAVASCRIPT_COURSES = listOf(2606L, 2223L, 156L, 217L, 1127L, 125L, 126L, 150L)
    private val KOTLIN_COURSES = listOf(2852L, 156L, 217L, 1127L, 125L, 126L, 150L)
    private val PYTHON_COURSES = listOf(67L, 512L, 401L, 568L, 431L, 2057L, 156L, 217L, 1127L, 125L, 126L, 150L)
    private val R_COURSES = listOf(497L, 129L, 724L, 156L, 217L, 1127L, 125L, 126L, 150L)
    private val RUBY_COURSES = listOf(156L, 1127L, 125L, 126L, 150L)
    private val SCALA_COURSES = listOf(2294L, 156L, 217L, 1127L, 125L, 126L, 150L)

    val COURSES = mapOf(
            ASM32 to ASM_COURSES,
            ASM64 to ASM_COURSES,
            C to DEFAULT_COURSES,
            CLOJURE to DEFAULT_COURSES,
            CPP to CPP_COURSES,
            CPP_11 to CPP_COURSES,
            GO to DEFAULT_COURSES,
            HASKELL to HASKELL_COURSES,
            HASKELL_7_10 to HASKELL_COURSES,
            HASKELL_8_0 to HASKELL_COURSES,
            JAVA7 to JAVA_COURSES,
            JAVA8 to JAVA_COURSES,
            JAVASCRIPT to JAVASCRIPT_COURSES,
            KOTLIN to KOTLIN_COURSES,
            MONO_CS to DEFAULT_COURSES,
            OCTAVE to DEFAULT_COURSES,
            PASCAL_ABC to DEFAULT_COURSES,
            PYTHON3 to PYTHON_COURSES,
            R to R_COURSES,
            RUBY to RUBY_COURSES,
            RUST to DEFAULT_COURSES,
            SCALA to SCALA_COURSES,
            SHELL to DEFAULT_COURSES
    )
}
