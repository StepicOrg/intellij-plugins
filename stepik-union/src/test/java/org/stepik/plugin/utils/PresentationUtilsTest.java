package org.stepik.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.jetbrains.tmp.learning.core.EduNames;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
@RunWith(PowerMockRunner.class)
public class PresentationUtilsTest {

    private static final String HOME_COURSE = "/home/course";
    private static final String SECTION_DIR = EduNames.SECTION + 1;
    private static final String SECTION_PATH = HOME_COURSE + "/" + SECTION_DIR;
    private static final String LESSON_DIR = EduNames.LESSON + 1;
    private static final String LESSON_PATH = SECTION_PATH + "/" + LESSON_DIR;
    private static final String TASK_DIR = EduNames.TASK + 1;
    private static final String TASK_PATH = LESSON_PATH + "/" + TASK_DIR;
    private static final String SRC_DIR = EduNames.SRC;
    private static final String SRC_PATH = TASK_PATH + "/" + SRC_DIR;
    private static final String HIDE_DIR = EduNames.HIDE;
    private static final String HIDE_PATH = SRC_PATH + "/" + HIDE_DIR;
    private static final String UTIL_DIR = EduNames.UTIL + 1;
    private static final String UTIL_PATH = HOME_COURSE + "/" + UTIL_DIR;
    private static final String SANDBOX_DIR = EduNames.SANDBOX_DIR;
    private static final String SANDBOX_PATH = HOME_COURSE + "/" + SANDBOX_DIR;

    private final Project project = PowerMockito.mock(Project.class);

    private final PsiDirectory courseDir = PowerMockito.mock(PsiDirectory.class);
    private final VirtualFile courseVF = PowerMockito.mock(VirtualFile.class);

    private final PsiDirectory sectionDir = PowerMockito.mock(PsiDirectory.class);
    private final VirtualFile sectionVF = PowerMockito.mock(VirtualFile.class);

    private final PsiDirectory lessonDir = PowerMockito.mock(PsiDirectory.class);
    private final VirtualFile lessonVF = PowerMockito.mock(VirtualFile.class);

    private final PsiDirectory taskDir = PowerMockito.mock(PsiDirectory.class);
    private final VirtualFile taskVF = PowerMockito.mock(VirtualFile.class);

    private final PsiDirectory srcDir = PowerMockito.mock(PsiDirectory.class);
    private final VirtualFile srcVF = PowerMockito.mock(VirtualFile.class);

    private final PsiDirectory hideDir = PowerMockito.mock(PsiDirectory.class);
    private final VirtualFile hideVF = PowerMockito.mock(VirtualFile.class);

    private final PsiDirectory utilDir = PowerMockito.mock(PsiDirectory.class);
    private final VirtualFile utilVF = PowerMockito.mock(VirtualFile.class);

    private final PsiDirectory sandboxDir = PowerMockito.mock(PsiDirectory.class);
    private final VirtualFile sandboxVF = PowerMockito.mock(VirtualFile.class);

    @Before
    public void before() {
        PowerMockito.when(project.getBasePath()).thenReturn(HOME_COURSE);

        PowerMockito.when(courseDir.getProject()).thenReturn(project);
        PowerMockito.when(sectionDir.getProject()).thenReturn(project);
        PowerMockito.when(lessonDir.getProject()).thenReturn(project);
        PowerMockito.when(taskDir.getProject()).thenReturn(project);
        PowerMockito.when(srcDir.getProject()).thenReturn(project);
        PowerMockito.when(hideDir.getProject()).thenReturn(project);
        PowerMockito.when(utilDir.getProject()).thenReturn(project);
        PowerMockito.when(sandboxDir.getProject()).thenReturn(project);

        PowerMockito.when(courseDir.getName()).thenReturn("course");
        PowerMockito.when(courseDir.getVirtualFile()).thenReturn(courseVF);
        PowerMockito.when(courseVF.getPath()).thenReturn(HOME_COURSE);

        PowerMockito.when(sectionDir.getName()).thenReturn(SECTION_DIR);
        PowerMockito.when(sectionDir.getVirtualFile()).thenReturn(sectionVF);
        PowerMockito.when(sectionVF.getPath()).thenReturn(SECTION_PATH);
        PowerMockito.when(sectionVF.getParent()).thenReturn(courseVF);

        PowerMockito.when(lessonDir.getName()).thenReturn(LESSON_DIR);
        PowerMockito.when(lessonDir.getVirtualFile()).thenReturn(lessonVF);
        PowerMockito.when(lessonVF.getPath()).thenReturn(LESSON_PATH);
        PowerMockito.when(lessonVF.getParent()).thenReturn(sectionVF);

        PowerMockito.when(taskDir.getName()).thenReturn(TASK_DIR);
        PowerMockito.when(taskDir.getVirtualFile()).thenReturn(taskVF);
        PowerMockito.when(taskVF.getPath()).thenReturn(TASK_PATH);
        PowerMockito.when(taskVF.getParent()).thenReturn(lessonVF);

        PowerMockito.when(srcDir.getName()).thenReturn(SRC_DIR);
        PowerMockito.when(srcDir.getVirtualFile()).thenReturn(srcVF);
        PowerMockito.when(srcVF.getPath()).thenReturn(SRC_PATH);
        PowerMockito.when(srcVF.getParent()).thenReturn(taskVF);

        PowerMockito.when(hideDir.getName()).thenReturn(HIDE_DIR);
        PowerMockito.when(hideDir.getVirtualFile()).thenReturn(hideVF);
        PowerMockito.when(hideVF.getPath()).thenReturn(HIDE_PATH);
        PowerMockito.when(hideVF.getParent()).thenReturn(srcVF);

        PowerMockito.when(utilDir.getName()).thenReturn(UTIL_DIR);
        PowerMockito.when(utilDir.getVirtualFile()).thenReturn(utilVF);
        PowerMockito.when(utilVF.getPath()).thenReturn(UTIL_PATH);
        PowerMockito.when(utilVF.getParent()).thenReturn(courseVF);

        PowerMockito.when(sandboxDir.getName()).thenReturn(SANDBOX_DIR);
        PowerMockito.when(sandboxDir.getVirtualFile()).thenReturn(sandboxVF);
        PowerMockito.when(sandboxVF.getPath()).thenReturn(SANDBOX_PATH);
        PowerMockito.when(sandboxVF.getParent()).thenReturn(courseVF);
    }

    @Test
    public void isVisibleSectionDirectory() throws Exception {
        assertEquals(true, PresentationUtils.isVisibleDirectory(sectionDir));
    }

    @Test
    public void isVisibleLessonDirectory() throws Exception {
        assertEquals(true, PresentationUtils.isVisibleDirectory(lessonDir));
    }

    @Test
    public void isVisibleTaskDirectory() throws Exception {
        assertEquals(true, PresentationUtils.isVisibleDirectory(taskDir));
    }

    @Test
    public void isVisibleSRCDirectory() throws Exception {
        assertEquals(true, PresentationUtils.isVisibleDirectory(srcDir));
    }

    @Test
    public void isVisibleHideDirectory() throws Exception {
        assertEquals(false, PresentationUtils.isVisibleDirectory(hideDir));
    }

    @Test
    public void isVisibleUtilDirectory() throws Exception {
        assertEquals(true, PresentationUtils.isVisibleDirectory(utilDir));
    }

    @Test
    public void isVisibleSandboxDirectory() throws Exception {
        assertEquals(true, PresentationUtils.isVisibleDirectory(sandboxDir));
    }

    @Test
    public void isVisibleFile() throws Exception {

    }

}