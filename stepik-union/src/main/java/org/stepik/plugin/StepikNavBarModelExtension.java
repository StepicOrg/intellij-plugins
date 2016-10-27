package org.stepik.plugin;

import com.intellij.ide.navigationToolbar.JavaNavBarExtension;
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
            PsiDirectory item = (PsiDirectory) object;
            Course course = getCourse(item.getProject());
            if (course == null)
                return null;

            if (item.getVirtualFile().equals(item.getProject().getBaseDir())) {
                return course.getName();
            }

            String name = item.getName();

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
        }

        return super.getPresentableText(object);
    }

    @Nullable
    private Course getCourse(@NotNull Project project) {
        StudyTaskManager studyTaskManager = StudyTaskManager.getInstance(project);
        return studyTaskManager.getCourse();
    }
}
