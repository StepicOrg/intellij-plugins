package org.stepik.plugin.utils;

import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.core.EduNames;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author meanmail
 */
@RunWith(Theories.class)
public class ProjectFilesUtilsTest {

    private static final String COURSE = "course";
    private static final String SECTION1 = EduNames.SECTION + 1;
    private static final String LESSON1 = EduNames.LESSON + 1;
    private static final String ABS_COURSE_SECTION1 = FileUtil.join("", COURSE, SECTION1);
    private static final String REL_COURSE_SECTION1 = FileUtil.join(COURSE, SECTION1);

    @DataPoints("paths")
    public static final String[][] paths = new String[][] {
            {"", ABS_COURSE_SECTION1, FileUtil.join(COURSE, SECTION1)},
            {File.separator, ABS_COURSE_SECTION1, REL_COURSE_SECTION1},
            {FileUtil.join("", COURSE), ABS_COURSE_SECTION1, SECTION1},
            {FileUtil.join("", COURSE, LESSON1), ABS_COURSE_SECTION1, FileUtil.join("..", SECTION1)},
            {".", FileUtil.join(".", REL_COURSE_SECTION1), REL_COURSE_SECTION1},
            {"lesson", ABS_COURSE_SECTION1, ABS_COURSE_SECTION1}
    };

    @Theory(nullsAccepted = false)
    public void getRelativePath(@FromDataPoints("paths") String[] paths) throws Exception {
        assertEquals(paths[2], ProjectFilesUtils.getRelativePath(paths[0], paths[1]));
    }

    private static final String TASK1 = EduNames.TASK + 1;

    @DataPoints("studyItems")
    public static final String[] studyItems = new String[] {
            ".",
            SECTION1,
            FileUtil.join(SECTION1, LESSON1),
            FileUtil.join(SECTION1, LESSON1, TASK1),
            FileUtil.join(SECTION1, LESSON1, TASK1, EduNames.SRC)
    };

    @Theory(nullsAccepted = false)
    public void isStudyItemDir(@FromDataPoints("studyItems") String relPath) throws Exception {
        assertTrue(ProjectFilesUtils.isStudyItemDir(relPath));
    }

    @DataPoints("notStudyItems")
    public static final String[] notStudyItems = new String[] {
            FileUtil.join(SECTION1, SECTION1),
            FileUtil.join(SECTION1, TASK1),
            FileUtil.join(SECTION1, EduNames.SRC),
            FileUtil.join(SECTION1, EduNames.TASK),
            FileUtil.join(SECTION1, EduNames.UTIL),
            FileUtil.join(SECTION1, EduNames.SANDBOX_DIR),
            FileUtil.join(LESSON1, SECTION1),
            FileUtil.join(SECTION1, SECTION1, TASK1),
            FileUtil.join(LESSON1, EduNames.TASK),
            FileUtil.join(LESSON1, EduNames.UTIL),
            FileUtil.join(LESSON1, EduNames.SANDBOX_DIR),
            EduNames.SECTION,
            EduNames.LESSON,
            EduNames.TASK,
            FileUtil.join(EduNames.SECTION, EduNames.LESSON, EduNames.TASK, EduNames.SRC),
            FileUtil.join(SECTION1, LESSON1 + EduNames.TASK, EduNames.SRC)
    };

    @Theory(nullsAccepted = false)
    public void isNotStudyItemDir(@FromDataPoints("notStudyItems") String relPath) throws Exception {
        assertFalse(ProjectFilesUtils.isStudyItemDir(relPath));
    }

}