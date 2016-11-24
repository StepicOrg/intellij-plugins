package org.stepik.plugin.projectView;

import com.intellij.ide.navigationToolbar.JavaNavBarExtension;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.courseFormat.Course;
import org.jetbrains.annotations.Nullable;

import static org.stepik.plugin.utils.PresentationDataUtils.isVisibleDirectory;
import static org.stepik.plugin.utils.PresentationDataUtils.isVisibleFile;
import static org.stepik.plugin.utils.PresentationDataUtils.updatePresentationData;

/**
 * @author meanmail
 */
public class StepikNavBarModelExtension extends JavaNavBarExtension {

    @Nullable
    @Override
    public String getPresentableText(@Nullable final Object object) {
        if (object instanceof Project) {
            Project project = (Project) object;
            StudyTaskManager studyTaskManager = StudyTaskManager.getInstance(project);
            Course course = studyTaskManager.getCourse();
            if (course == null)
                return null;
            return course.getName();
        }

        if (object instanceof PsiDirectory) {
            PsiDirectory psiDirectory = (PsiDirectory) object;
            PresentationData data = new PresentationData();
            updatePresentationData(data, psiDirectory);
            String text = data.getPresentableText();
            if (text != null)
                return text;
        }

        return super.getPresentableText(object);
    }

    @Nullable
    @Override
    public PsiElement adjustElement(final PsiElement psiElement) {
        if (psiElement instanceof PsiDirectory) {
            if (!isVisibleDirectory((PsiDirectory) psiElement))
                return null;
        } else if (psiElement instanceof PsiFile) {
            if (!isVisibleFile((PsiFile) psiElement))
                return null;
        }

        return super.adjustElement(psiElement);
    }
}
