package org.stepik.plugin.utils;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.utils.PresentationUtils;
import org.stepik.core.utils.ProjectFilesUtils;

import javax.swing.*;

import static org.stepik.core.utils.PresentationUtils.getColor;
import static org.stepik.core.utils.PresentationUtils.getIcon;
import static org.stepik.core.utils.ProjectFilesUtils.isSandbox;
import static org.stepik.plugin.utils.ProjectPsiFilesUtils.getRelativePath;

/**
 * @author meanmail
 */
public class PresentationDataUtils {

    public static void updatePresentationData(@NotNull PresentationData data, @NotNull PsiDirectory psiDirectory) {
        Project project = psiDirectory.getProject();
        String valueName = psiDirectory.getName();

        StudyNode root = StepikProjectManager.getProjectRoot(project);
        if (root == null) {
            return;
        }

        String path = getRelativePath(psiDirectory);
        if (isSandbox(path)) {
            setAttributes(data, EduNames.SANDBOX_DIR, JBColor.BLACK, AllStepikIcons.ProjectTree.sandbox, false);
            return;
        }

        path = ".".equals(path) ? "" : path;

        StudyNode node = StudyUtils.getStudyNode(project, psiDirectory.getVirtualFile());
        if (node != null && path.equals(node.getPath())) {
            setAttributes(data, node);
        } else {
            data.setPresentableText(valueName);
        }
    }

    private static void setAttributes(@NotNull PresentationData data, @NotNull StudyNode item) {
        String text = item.getName();
        StudyStatus status = item.getStatus();
        JBColor color = getColor(status);
        Icon icon = getIcon(item.getClass(), status);
        setAttributes(data, text, color, icon, item.getWasDeleted());
    }

    private static void setAttributes(
            @NotNull PresentationData data,
            String text,
            JBColor color,
            Icon icon,
            boolean deleted) {
        data.clearText();
        int textStyle = SimpleTextAttributes.STYLE_PLAIN;
        if (deleted) {
            textStyle |= SimpleTextAttributes.STYLE_STRIKEOUT;
        }
        data.addText(text, new SimpleTextAttributes(textStyle, color));
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
