package org.stepik.plugin;

import com.intellij.ide.navigationToolbar.NavBarModelExtension;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author meanmail
 */
public class StepikNavBarModelExtension implements NavBarModelExtension {

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
            PsiDirectory item = (PsiDirectory) object;
            Course course = getCourse(item.getProject());
            if (course == null)
                return null;
            String name = item.getName();

            if (item.getVirtualFile().equals(item.getProject().getBaseDir())) {
                return course.getName();
            }

            if (name.startsWith(EduNames.SECTION)) {
                return course.getSectionsNames().get(name);
            }

            if (name.startsWith(EduNames.LESSON)) {
                return course.getLesson(name).getName();
            }

            if (name.startsWith(EduNames.TASK)) {
                PsiDirectory lessonItem = item.getParent();
                if (lessonItem == null)
                    return null;
                String lessonName = lessonItem.getName();
                Lesson lesson = course.getLessons().get(EduUtils.getIndex(lessonName, EduNames.LESSON));
                return lesson.getTask(name).getName();
            }

            return null;
        } else {
            return null;
        }
    }
    @Nullable
    private Course getCourse(@NotNull Project project) {
        StudyTaskManager studyTaskManager = StudyTaskManager.getInstance(project);
        return studyTaskManager.getCourse();
    }

    @Nullable
    @Override
    public PsiElement getParent(PsiElement psiElement) {
        return psiElement.getParent();
    }

    @Nullable
    @Override
    public PsiElement adjustElement(PsiElement psiElement) {
        return psiElement;
    }

    @NotNull
    @Override
    public Collection<VirtualFile> additionalRoots(Project project) {
        return Collections.emptyList();
    }
}
