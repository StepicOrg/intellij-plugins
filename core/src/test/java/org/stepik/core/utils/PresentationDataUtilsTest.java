package org.stepik.core.utils;

import com.jetbrains.tmp.learning.core.EduNames;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.stepik.core.TestUtils.join;

/**
 * @author meanmail
 */
@RunWith(Theories.class)
public class PresentationDataUtilsTest {
    private static final String BASE_DIR = ".";
    private static final String SECTION_DIR = EduNames.SECTION + 1;
    private static final String SECTION_PATH = EduNames.SECTION + 1;
    private static final String LESSON_PATH = join(SECTION_PATH, EduNames.LESSON + 1);
    private static final String TASK_PATH = join(LESSON_PATH, EduNames.STEP + 1);
    private static final String SRC_PATH = join(TASK_PATH, EduNames.SRC);
    @DataPoints("visiblePaths")
    public static final String[] visiblePaths = new String[]{
            BASE_DIR,
            SECTION_DIR,
            SECTION_PATH,
            LESSON_PATH,
            TASK_PATH,
            SRC_PATH,
            EduNames.SANDBOX_DIR,
            join(SRC_PATH, SECTION_DIR)
    };
    private static final String HIDE_PATH = join(SRC_PATH, EduNames.HIDE);
    @SuppressWarnings("unused")
    @DataPoints("nonVisiblePaths")
    public static final String[] nonVisiblePaths = new String[]{
            join(SECTION_PATH, SECTION_DIR),
            join(LESSON_PATH, SECTION_DIR),
            join(TASK_PATH, SECTION_DIR),
            HIDE_PATH
    };
    private static final String FILE = "file.txt";
    @SuppressWarnings("unused")
    @DataPoints("visibleFiles")
    public static final String[] visibleFiles = new String[]{
            join(EduNames.SANDBOX_DIR, FILE)
    };
    @SuppressWarnings("unused")
    @DataPoints("nonVisibleFiles")
    public static final String[] nonVisibleFiles = new String[]{
            BASE_DIR,
            EduNames.SANDBOX_DIR,
            FILE,
            join(SECTION_PATH, FILE),
            join(LESSON_PATH, FILE),
            join(TASK_PATH, FILE),
            join(TASK_PATH, EduNames.SANDBOX_DIR, FILE),
            join(HIDE_PATH, FILE)
    };

    @Theory(nullsAccepted = false)
    public void directoryIsVisible(@FromDataPoints("visiblePaths") String directory) throws Exception {
        assertTrue(PresentationUtils.isVisibleDirectory(directory));
    }

    @Theory(nullsAccepted = false)
    public void directoryIsNonVisible(@FromDataPoints("nonVisiblePaths") String directory) throws Exception {
        assertFalse(PresentationUtils.isVisibleDirectory(directory));
    }

    @Theory(nullsAccepted = false)
    public void fileIsVisible(@FromDataPoints("visibleFiles") String file) throws Exception {
        assertTrue(PresentationUtils.isVisibleFile(file));
    }

    @Theory(nullsAccepted = false)
    public void fileIsNonVisible(@FromDataPoints("nonVisibleFiles") String file) throws Exception {
        assertFalse(PresentationUtils.isVisibleFile(file));
    }
}