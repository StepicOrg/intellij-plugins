package org.stepik.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.stepik.core.EduNames.HIDE
import org.stepik.core.EduNames.LESSON
import org.stepik.core.EduNames.SANDBOX_DIR
import org.stepik.core.EduNames.SECTION
import org.stepik.core.EduNames.SRC
import org.stepik.core.EduNames.STEP
import org.stepik.core.join


@RunWith(Theories::class)
class ProjectFilesUtilsTest {

    @Theory(nullsAccepted = false)
    fun getRelativePath(@FromDataPoints("paths") paths: Array<String>) {
        assertEquals(paths[2], paths[0].getRelativePath(paths[1]))
    }

    @Theory(nullsAccepted = false)
    fun isStudyItemDir(@FromDataPoints("studyItems") relPath: String) {
        assertTrue(relPath.isStudyItemDir())
    }

    @Theory(nullsAccepted = false)
    fun isNotStudyItemDir(@FromDataPoints("notStudyItems") relPath: String) {
        assertFalse(relPath.isStudyItemDir())
    }

    @Theory
    fun isCanBeTarget(@FromDataPoints("validTargets") targetPath: String) {
        assertFalse(targetPath.isNotTarget())
    }

    @Theory
    fun isCanNotBeTarget(@FromDataPoints("notValidTarget") targetPath: String) {
        assertTrue(targetPath.isNotTarget())
    }

    @Test
    fun isSandbox() {
        assertTrue(SANDBOX_DIR.isSandbox())
    }

    @Test
    fun isWithinSandbox() {
        val within = join(SANDBOX_DIR, "other")
        assertTrue(within.isWithinSandbox())
    }

    @Test
    fun isWithinSrc() {
        val within = join(SECTION1_LESSON1_TASK1_SRC, "other")
        assertTrue(within.isWithinSrc())
    }

    @Test
    fun isWithinHideDir() {
        val within = join(SECTION1_LESSON1_TASK1_SRC, HIDE, "other")
        assertTrue(within.isWithinHideDir())
    }

    @Test
    fun isHideDir() {
        val hide = join(SECTION1_LESSON1_TASK1_SRC, HIDE)
        assertTrue(hide.isHideDir())
    }

    @Test
    fun getParentForEmpty() {
        assertEquals(null, getParent(""))
    }

    @Test
    fun getParentForSingle() {
        assertEquals(".", getParent(SECTION1))
    }

    @Test
    fun getParent() {
        val parent = join(SECTION1, LESSON1, TASK1)
        assertEquals(parent, getParent(SECTION1_LESSON1_TASK1_SRC))
    }

    @Test
    fun getParentForDot() {
        assertEquals(null, getParent("."))
    }

    companion object {
        private const val COURSE = "course"
        private const val SECTION1 = SECTION + 1
        private const val LESSON1 = LESSON + 1
        private val ABS_COURSE_SECTION1 = join("", COURSE, SECTION1)
        private val REL_COURSE_SECTION1 = join(COURSE, SECTION1)

        @DataPoints("paths")
        @JvmField
        val paths = arrayOf(
                arrayOf(SEPARATOR, ABS_COURSE_SECTION1, REL_COURSE_SECTION1),
                arrayOf(join("", COURSE), ABS_COURSE_SECTION1, SECTION1),
                arrayOf(join("", COURSE, LESSON1), ABS_COURSE_SECTION1, join("..", SECTION1)),
                arrayOf(".", join(".", REL_COURSE_SECTION1), REL_COURSE_SECTION1),
                arrayOf("lesson", ABS_COURSE_SECTION1, ABS_COURSE_SECTION1)
        )
        private const val TASK1 = STEP + 1

        @DataPoints("notStudyItems")
        @JvmField
        val notStudyItems = arrayOf(
                join(SECTION1, SECTION1),
                join(SECTION1, TASK1),
                join(SECTION1, SRC),
                join(SECTION1, STEP),
                join(SECTION1, SANDBOX_DIR),
                join(LESSON1, SECTION1),
                join(SECTION1, SECTION1, TASK1),
                join(LESSON1, STEP),
                join(LESSON1, SANDBOX_DIR),
                SECTION,
                LESSON,
                STEP,
                join(SECTION, LESSON, STEP, SRC),
                join(SECTION1, LESSON1 + STEP, SRC),
                SANDBOX_DIR
        )
        private val SECTION1_LESSON1_TASK1_SRC = join(SECTION1, LESSON1, TASK1, SRC)

        @DataPoints("studyItems")
        @JvmField
        val studyItems = arrayOf(
                ".",
                SECTION1,
                join(SECTION1, LESSON1),
                join(SECTION1, LESSON1, TASK1),
                SECTION1_LESSON1_TASK1_SRC
        )

        @DataPoints("validTargets")
        @JvmField
        var validTargets = arrayOf(
                SANDBOX_DIR,
                SECTION1_LESSON1_TASK1_SRC,
                join(SANDBOX_DIR, SECTION1),
                join(SANDBOX_DIR, "other"),
                join(SECTION1_LESSON1_TASK1_SRC, SECTION1),
                join(SECTION1_LESSON1_TASK1_SRC, "other")
        )

        @DataPoints("notValidTarget")
        @JvmField
        var notValidTarget = arrayOf(
                ".",
                SECTION1,
                join(SECTION1, LESSON1),
                join(SECTION1, LESSON1, TASK1)
        )
    }
}
