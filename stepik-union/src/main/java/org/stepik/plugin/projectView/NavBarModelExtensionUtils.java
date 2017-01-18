package org.stepik.plugin.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import org.jetbrains.annotations.Nullable;

import static org.stepik.plugin.utils.PresentationDataUtils.isVisibleDirectory;
import static org.stepik.plugin.utils.PresentationDataUtils.isVisibleFile;
import static org.stepik.plugin.utils.PresentationDataUtils.updatePresentationData;

/**
 * @author meanmail
 */
public class NavBarModelExtensionUtils {
    @Nullable
    public static String getPresentableText(@Nullable final Object object) {
        if (object instanceof Project) {
            Project project = (Project) object;
            StepikProjectManager stepikProjectManager = StepikProjectManager.getInstance(project);
            CourseNode courseNode = stepikProjectManager.getCourseNode();
            if (courseNode == null)
                return null;
            return courseNode.getName();
        }

        if (object instanceof PsiDirectory) {
            PsiDirectory psiDirectory = (PsiDirectory) object;
            PresentationData data = new PresentationData();
            updatePresentationData(data, psiDirectory);
            String text = data.getPresentableText();
            if (text != null)
                return text;
        }

        return null;
    }

    @Nullable
    public static PsiElement adjustElement(final PsiElement psiElement) {
        Project project = psiElement.getProject();
        StepikProjectManager stepikProjectManager = StepikProjectManager.getInstance(project);
        CourseNode courseNode = stepikProjectManager.getCourseNode();
        if (courseNode == null)
            return psiElement;

        if (psiElement instanceof PsiDirectory) {
            if (!isVisibleDirectory((PsiDirectory) psiElement))
                return null;
        } else if (psiElement instanceof PsiFile) {
            if (!isVisibleFile((PsiFile) psiElement))
                return null;
        }

        return psiElement;
    }
}
