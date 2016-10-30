package org.stepik.plugin;

import com.intellij.ide.navigationToolbar.JavaNavBarExtension;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.courseFormat.Course;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.plugin.utils.PresentationUtils;

import static org.stepik.plugin.utils.PresentationUtils.isVisibleDirectory;
import static org.stepik.plugin.utils.PresentationUtils.isVisibleFile;

/**
 * @author meanmail
 */
public class StepikNavBarModelExtension extends JavaNavBarExtension {

    @Nullable
    @Override
    public String getPresentableText(Object object) {
        if (object instanceof Project) {
            Project project = (Project) object;
            Course course = getCourse(project);
            if (course == null)
                return null;
            return course.getName();
        }

        if (object instanceof PsiDirectory) {
            PsiDirectory psiDirectory = (PsiDirectory) object;

            PresentationData data = new PresentationData();

            PresentationUtils.updatePresentationData(data, psiDirectory);

            String text = data.getPresentableText();
            if (text != null)
                return text;
        }

        return super.getPresentableText(object);
    }

    @Nullable
    private Course getCourse(@NotNull Project project) {
        StudyTaskManager studyTaskManager = StudyTaskManager.getInstance(project);
        return studyTaskManager.getCourse();
    }

    @Nullable
    @Override
    public PsiElement adjustElement(PsiElement psiElement) {
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
