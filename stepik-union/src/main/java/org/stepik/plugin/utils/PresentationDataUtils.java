package org.stepik.plugin.utils;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.utils.PresentationUtils;
import org.stepik.core.utils.ProjectFilesUtils;

import javax.swing.*;

import static org.stepik.core.utils.PresentationUtils.getColor;
import static org.stepik.core.utils.PresentationUtils.getIcon;
import static org.stepik.plugin.utils.ProjectPsiFilesUtils.getRelativePath;

/**
 * @author meanmail
 */
public class PresentationDataUtils {

    public static void updatePresentationData(@NotNull PresentationData data, @NotNull PsiDirectory psiDirectory) {
        Project project = psiDirectory.getProject();
        String valueName = psiDirectory.getName();
        StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
        if (projectManager == null) {
            return;
        }
        CourseNode courseNode = projectManager.getCourseNode();
        if (courseNode == null) {
            return;
        }
        VirtualFile baseDir = project.getBaseDir();
        String name = baseDir.getName();
        if (valueName.equals(name)) {
            setAttributes(data, courseNode);
        } else if (valueName.startsWith(EduNames.STEP)) {
            PsiDirectory lessonDirectory = psiDirectory.getParent();
            if (lessonDirectory != null) {
                LessonNode lessonNode = courseNode.getLessonByDirName(lessonDirectory.getName());
                if (lessonNode != null) {
                    StepNode stepNode = lessonNode.getStep(psiDirectory.getName());
                    if (stepNode != null) {
                        setAttributes(data, stepNode);
                    }
                }
            }
        } else if (valueName.startsWith(EduNames.LESSON)) {
            PsiDirectory parent = psiDirectory.getParent();
            if (parent == null) {
                return;
            }
            LessonNode lessonNode = courseNode.getLessonByDirName(valueName);
            if (lessonNode == null) {
                return;
            }
            setAttributes(data, lessonNode);
        } else if (valueName.startsWith(EduNames.SECTION)) {
            StudyNode sectionNode = courseNode.getSectionByDirName(valueName);
            if (sectionNode != null) {
                setAttributes(data, sectionNode);
            }
        } else if (valueName.contains(EduNames.SANDBOX_DIR)) {
            PsiDirectory parent = psiDirectory.getParent();
            if (parent != null) {
                if (!parent.getName().contains(EduNames.SANDBOX_DIR)) {
                    setAttributes(data, EduNames.SANDBOX_DIR, JBColor.BLACK, AllStepikIcons.ProjectTree.sandbox);
                }
            }
        } else
            data.setPresentableText(valueName);
    }

    private static void setAttributes(@NotNull PresentationData data, @NotNull StudyNode item) {
        String text = item.getName();
        StudyStatus status = item.getStatus();
        JBColor color = getColor(status);
        Icon icon = getIcon(item.getClass(), status);
        setAttributes(data, text, color, icon);
    }

    private static void setAttributes(@NotNull PresentationData data, String text, JBColor color, Icon icon) {
        data.clearText();
        data.addText(text, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, color));
        data.setIcon(icon);
        data.setPresentableText(text);
    }

    public static boolean isVisibleDirectory(@NotNull PsiDirectory psiDirectory) {
        Project project = psiDirectory.getProject();
        String basePath = project.getBasePath();
        if (basePath == null) {
            return false;
        }
        String path = psiDirectory.getVirtualFile().getPath();
        String relPath = ProjectFilesUtils.getRelativePath(basePath, path);

        return PresentationUtils.isVisibleDirectory(relPath);
    }

    public static boolean isVisibleFile(@NotNull PsiFile psiFile) {
        String path = getRelativePath(psiFile);
        return PresentationUtils.isVisibleFile(path);
    }
}
