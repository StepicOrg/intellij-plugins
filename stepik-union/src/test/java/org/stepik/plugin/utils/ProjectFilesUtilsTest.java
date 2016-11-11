package org.stepik.plugin.utils;

import com.jetbrains.tmp.learning.core.EduNames;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.stepik.plugin.utils.ProjectFilesUtils.SEPARATOR;
import static utils.TestUtils.join;

/**
 * @author meanmail
 */
@RunWith(Theories.class)
public class ProjectFilesUtilsTest {
    private static final String COURSE = "course";
    private static final String SECTION1 = EduNames.SECTION + 1;
    private static final String LESSON1 = EduNames.LESSON + 1;
    private static final String ABS_COURSE_SECTION1 = join("", COURSE, SECTION1);
    private static final String REL_COURSE_SECTION1 = join(COURSE, SECTION1);

    @SuppressWarnings("unused")
    @DataPoints("paths")
    public static final String[][] paths = new String[][]{
            {SEPARATOR, ABS_COURSE_SECTION1, REL_COURSE_SECTION1},
            {join("", COURSE), ABS_COURSE_SECTION1, SECTION1},
            {join("", COURSE, LESSON1), ABS_COURSE_SECTION1, join("..", SECTION1)},
            {".", join(".", REL_COURSE_SECTION1), REL_COURSE_SECTION1},
            {"lesson", ABS_COURSE_SECTION1, ABS_COURSE_SECTION1}
    };

    @Theory(nullsAccepted = false)
    public void getRelativePath(@FromDataPoints("paths") String[] paths) throws Exception {
        assertEquals(paths[2], ProjectFilesUtils.getRelativePath(paths[0], paths[1]));
    }

    private static final String TASK1 = EduNames.TASK + 1;

    private static final String SECTION1_LESSON1_TASK1_SRC = join(SECTION1, LESSON1, TASK1, EduNames.SRC);

    @SuppressWarnings("unused")
    @DataPoints("studyItems")
    public static final String[] studyItems = new String[]{
            ".",
            SECTION1,
            join(SECTION1, LESSON1),
            join(SECTION1, LESSON1, TASK1),
            SECTION1_LESSON1_TASK1_SRC
    };

    @Theory(nullsAccepted = false)
    public void isStudyItemDir(@FromDataPoints("studyItems") String relPath) throws Exception {
        assertTrue(ProjectFilesUtils.isStudyItemDir(relPath));
    }

    @SuppressWarnings("unused")
    @DataPoints("notStudyItems")
    public static final String[] notStudyItems = new String[]{
            join(SECTION1, SECTION1),
            join(SECTION1, TASK1),
            join(SECTION1, EduNames.SRC),
            join(SECTION1, EduNames.TASK),
            join(SECTION1, EduNames.UTIL),
            join(SECTION1, EduNames.SANDBOX_DIR),
            join(LESSON1, SECTION1),
            join(SECTION1, SECTION1, TASK1),
            join(LESSON1, EduNames.TASK),
            join(LESSON1, EduNames.UTIL),
            join(LESSON1, EduNames.SANDBOX_DIR),
            EduNames.SECTION,
            EduNames.LESSON,
            EduNames.TASK,
            join(EduNames.SECTION, EduNames.LESSON, EduNames.TASK, EduNames.SRC),
            join(SECTION1, LESSON1 + EduNames.TASK, EduNames.SRC),
            EduNames.UTIL,
            EduNames.SANDBOX_DIR
    };

    @Theory(nullsAccepted = false)
    public void isNotStudyItemDir(@FromDataPoints("notStudyItems") String relPath) throws Exception {
        assertFalse(ProjectFilesUtils.isStudyItemDir(relPath));
    }

    @SuppressWarnings("unused")
    @DataPoints("validTargets")
    public static String[] validTargets = new String[]{
            EduNames.SANDBOX_DIR,
            SECTION1_LESSON1_TASK1_SRC,
            join(EduNames.SANDBOX_DIR, SECTION1),
            join(EduNames.SANDBOX_DIR, "other"),
            join(SECTION1_LESSON1_TASK1_SRC, SECTION1),
            join(SECTION1_LESSON1_TASK1_SRC, "other")
    };

    @SuppressWarnings("unused")
    @DataPoints("notValidTarget")
    public static String[] notValidTarget = new String[]{
            ".",
            SECTION1,
            join(SECTION1, LESSON1),
            join(SECTION1, LESSON1, TASK1)
    };

    @Theory
    public void isCanBeTarget(@FromDataPoints("validTargets") String targetPath) throws Exception {
        assertFalse(ProjectFilesUtils.isCanNotBeTarget(targetPath));
    }

    @Theory
    public void isCanNotBeTarget(@FromDataPoints("notValidTarget") String targetPath) throws Exception {
        assertTrue(ProjectFilesUtils.isCanNotBeTarget(targetPath));
    }

    @Test
    public void isTaskHtmlFile() throws Exception {
        String taskFile = join(SECTION1_LESSON1_TASK1_SRC, EduNames.TASK_HTML);
        assertTrue(ProjectFilesUtils.isTaskHtmlFile(taskFile));
    }

    @Test
    public void isSandbox() throws Exception {
        assertTrue(ProjectFilesUtils.isSandbox(EduNames.SANDBOX_DIR));
    }

    @Test
    public void isWithinSandbox() throws Exception {
        String within = join(EduNames.SANDBOX_DIR, "other");
        assertTrue(ProjectFilesUtils.isWithinSandbox(within));
    }

    @Test
    public void isWithinUtil() throws Exception {
        String within = join(EduNames.UTIL, "other");
        assertTrue(ProjectFilesUtils.isWithinUtil(within));
    }

    @Test
    public void isWithinSrc() throws Exception {
        String within = join(SECTION1_LESSON1_TASK1_SRC, "other");
        assertTrue(ProjectFilesUtils.isWithinSrc(within));
    }

    @Test
    public void isUtilDir() throws Exception {
        assertTrue(ProjectFilesUtils.isUtilDir(EduNames.UTIL));
    }

    @Test
    public void isWithinHideDir() throws Exception {
        String within = join(SECTION1_LESSON1_TASK1_SRC, EduNames.HIDE, "other");
        assertTrue(ProjectFilesUtils.isWithinHideDir(within));
    }

    @Test
    public void isHideDir() throws Exception {
        String hide = join(SECTION1_LESSON1_TASK1_SRC, EduNames.HIDE);
        assertTrue(ProjectFilesUtils.isHideDir(hide));
    }

    @Test
    public void getParentForEmpty() throws Exception {
        assertEquals(null, ProjectFilesUtils.getParent(""));
    }

    @Test
    public void getParentForSingle() throws Exception {
        assertEquals(".", ProjectFilesUtils.getParent(SECTION1));
    }

    @Test
    public void getParent() throws Exception {
        String parent = join(SECTION1, LESSON1, TASK1);
        assertEquals(parent, ProjectFilesUtils.getParent(SECTION1_LESSON1_TASK1_SRC));
    }

    @Test
    public void getParentForDot() throws Exception {
        assertEquals(null, ProjectFilesUtils.getParent("."));
    }
}