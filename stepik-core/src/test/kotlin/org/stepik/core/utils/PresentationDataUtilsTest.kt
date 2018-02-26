package org.stepik.core.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.stepik.core.EduNames
import org.stepik.core.TestUtils.join


@RunWith(Theories::class)
class PresentationDataUtilsTest {

    @Theory(nullsAccepted = false)
    fun directoryIsVisible(@FromDataPoints("visiblePaths") directory: String) {
        assertTrue(PresentationUtils.isVisibleDirectory(directory))
    }

    @Theory(nullsAccepted = false)
    fun directoryIsNonVisible(@FromDataPoints("nonVisiblePaths") directory: String) {
        assertFalse(PresentationUtils.isVisibleDirectory(directory))
    }

    @Theory(nullsAccepted = false)
    fun fileIsVisible(@FromDataPoints("visibleFiles") file: String) {
        assertTrue(PresentationUtils.isVisibleFile(file))
    }

    @Theory(nullsAccepted = false)
    fun fileIsNonVisible(@FromDataPoints("nonVisibleFiles") file: String) {
        assertFalse(PresentationUtils.isVisibleFile(file))
    }

    companion object {
        private const val BASE_DIR = "."
        private const val SECTION_DIR = EduNames.SECTION + 1
        private const val SECTION_PATH = EduNames.SECTION + 1
        private val LESSON_PATH = join(SECTION_PATH, EduNames.LESSON + 1)
        private val TASK_PATH = join(LESSON_PATH, EduNames.STEP + 1)
        private val SRC_PATH = join(TASK_PATH, EduNames.SRC)

        @DataPoints("visiblePaths")
        @JvmField
        val visiblePaths = arrayOf(
                BASE_DIR,
                SECTION_DIR,
                SECTION_PATH,
                LESSON_PATH,
                TASK_PATH,
                SRC_PATH,
                EduNames.SANDBOX_DIR,
                join(SRC_PATH, SECTION_DIR)
        )
        private val HIDE_PATH = join(SRC_PATH, EduNames.HIDE)

        @DataPoints("nonVisiblePaths")
        @JvmField
        val nonVisiblePaths = arrayOf(
                join(SECTION_PATH, SECTION_DIR),
                join(LESSON_PATH, SECTION_DIR),
                join(TASK_PATH, SECTION_DIR), HIDE_PATH
        )

        private const val FILE = "file.txt"

        @DataPoints("visibleFiles")
        @JvmField
        val visibleFiles = arrayOf(join(EduNames.SANDBOX_DIR, FILE))

        @DataPoints("nonVisibleFiles")
        @JvmField
        val nonVisibleFiles = arrayOf(
                BASE_DIR,
                EduNames.SANDBOX_DIR,
                FILE,
                join(SECTION_PATH, FILE),
                join(LESSON_PATH, FILE),
                join(TASK_PATH, FILE),
                join(TASK_PATH, EduNames.SANDBOX_DIR, FILE),
                join(HIDE_PATH, FILE)
        )
    }
}
