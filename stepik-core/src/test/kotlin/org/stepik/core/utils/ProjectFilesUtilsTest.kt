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
import org.stepik.core.EduNames
import org.stepik.core.TestUtils.join


@RunWith(Theories::class)
class ProjectFilesUtilsTest {

    @Theory(nullsAccepted = false)
    fun getRelativePath(@FromDataPoints("paths") paths: Array<String>) {
        assertEquals(paths[2], ProjectFilesUtils.getRelativePath(paths[0], paths[1]))
    }

    @Theory(nullsAccepted = false)
    fun isStudyItemDir(@FromDataPoints("studyItems") relPath: String) {
        assertTrue(ProjectFilesUtils.isStudyItemDir(relPath))
    }

    @Theory(nullsAccepted = false)
    fun isNotStudyItemDir(@FromDataPoints("notStudyItems") relPath: String) {
        assertFalse(ProjectFilesUtils.isStudyItemDir(relPath))
    }

    @Theory
    fun isCanBeTarget(@FromDataPoints("validTargets") targetPath: String) {
        assertFalse(ProjectFilesUtils.isCanNotBeTarget(targetPath))
    }

    @Theory
    fun isCanNotBeTarget(@FromDataPoints("notValidTarget") targetPath: String) {
        assertTrue(ProjectFilesUtils.isCanNotBeTarget(targetPath))
    }

    @Test
    fun isSandbox() {
        assertTrue(ProjectFilesUtils.isSandbox(EduNames.SANDBOX_DIR))
    }

    @Test
    fun isWithinSandbox() {
        val within = join(EduNames.SANDBOX_DIR, "other")
        assertTrue(ProjectFilesUtils.isWithinSandbox(within))
    }

    @Test
    fun isWithinSrc() {
        val within = join(SECTION1_LESSON1_TASK1_SRC, "other")
        assertTrue(ProjectFilesUtils.isWithinSrc(within))
    }

    @Test
    fun isWithinHideDir() {
        val within = join(SECTION1_LESSON1_TASK1_SRC, EduNames.HIDE, "other")
        assertTrue(ProjectFilesUtils.isWithinHideDir(within))
    }

    @Test
    fun isHideDir() {
        val hide = join(SECTION1_LESSON1_TASK1_SRC, EduNames.HIDE)
        assertTrue(ProjectFilesUtils.isHideDir(hide))
    }

    @Test
    fun getParentForEmpty() {
        assertEquals(null, ProjectFilesUtils.getParent(""))
    }

    @Test
    fun getParentForSingle() {
        assertEquals(".", ProjectFilesUtils.getParent(SECTION1))
    }

    @Test
    fun getParent() {
        val parent = join(SECTION1, LESSON1, TASK1)
        assertEquals(parent, ProjectFilesUtils.getParent(SECTION1_LESSON1_TASK1_SRC))
    }

    @Test
    fun getParentForDot() {
        assertEquals(null, ProjectFilesUtils.getParent("."))
    }

    companion object {
        private const val COURSE = "course"
        private const val SECTION1 = EduNames.SECTION + 1
        private const val LESSON1 = EduNames.LESSON + 1
        private val ABS_COURSE_SECTION1 = join("", COURSE, SECTION1)
        private val REL_COURSE_SECTION1 = join(COURSE, SECTION1)

        @DataPoints("paths")
        @JvmField
        val paths = arrayOf(
                arrayOf(ProjectFilesUtils.SEPARATOR, ABS_COURSE_SECTION1, REL_COURSE_SECTION1),
                arrayOf(join("", COURSE), ABS_COURSE_SECTION1, SECTION1),
                arrayOf(join("", COURSE, LESSON1), ABS_COURSE_SECTION1, join("..", SECTION1)),
                arrayOf(".", join(".", REL_COURSE_SECTION1), REL_COURSE_SECTION1),
                arrayOf("lesson", ABS_COURSE_SECTION1, ABS_COURSE_SECTION1)
        )
        private const val TASK1 = EduNames.STEP + 1

        @DataPoints("notStudyItems")
        @JvmField
        val notStudyItems = arrayOf(
                join(SECTION1, SECTION1),
                join(SECTION1, TASK1),
                join(SECTION1, EduNames.SRC),
                join(SECTION1, EduNames.STEP),
                join(SECTION1, EduNames.SANDBOX_DIR),
                join(LESSON1, SECTION1),
                join(SECTION1, SECTION1, TASK1),
                join(LESSON1, EduNames.STEP),
                join(LESSON1, EduNames.SANDBOX_DIR),
                EduNames.SECTION,
                EduNames.LESSON,
                EduNames.STEP,
                join(EduNames.SECTION, EduNames.LESSON, EduNames.STEP, EduNames.SRC),
                join(SECTION1, LESSON1 + EduNames.STEP, EduNames.SRC),
                EduNames.SANDBOX_DIR
        )
        private val SECTION1_LESSON1_TASK1_SRC = join(SECTION1, LESSON1, TASK1, EduNames.SRC)

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
                EduNames.SANDBOX_DIR,
                SECTION1_LESSON1_TASK1_SRC,
                join(EduNames.SANDBOX_DIR, SECTION1),
                join(EduNames.SANDBOX_DIR, "other"),
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
