package org.stepik.plugin.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.SectionNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.utils.PresentationDataUtils;

/**
 * @author meanmail
 */
class StepikDirectoryNode extends PsiDirectoryNode {

    StepikDirectoryNode(
            Project project,
            PsiDirectory value,
            ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    @Override
    protected void updateImpl(@NotNull PresentationData data) {
        PresentationDataUtils.updatePresentationData(data, getValue());
    }

    @Override
    public int getTypeSortWeight(boolean sortByType) {
        String name = getValue().getName();
        StepikProjectManager stepManager = StepikProjectManager.getInstance(getValue().getProject());
        CourseNode courseNode = stepManager.getCourseNode();
        if (courseNode == null) {
            return 0;
        }

        if (name.startsWith(EduNames.SECTION)) {
            int id = EduUtils.parseDirName(name, EduNames.SECTION);
            SectionNode sectionNode = courseNode.getSectionById(id);
            if (sectionNode == null) {
                return id;
            }
            return sectionNode.getPosition();
        }
        if (name.startsWith(EduNames.LESSON)) {
            int id = EduUtils.parseDirName(name, EduNames.LESSON);
            LessonNode lessonNode = courseNode.getLessonById(id);
            if (lessonNode == null) {
                return id;
            }
            return lessonNode.getPosition();
        }
        if (name.startsWith(EduNames.STEP)) {
            int id = EduUtils.parseDirName(name, EduNames.STEP);
            StepNode stepNode = courseNode.getStepById(id);
            if (stepNode == null) {
                return id;
            }
            return stepNode.getPosition();
        }

        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    @Override
    protected boolean hasProblemFileBeneath() {
        return false;
    }

    @Override
    public String getNavigateActionText(boolean focusEditor) {
        return null;
    }
}
