package org.stepik.plugin.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.utils.PresentationDataUtils;

import static org.stepik.plugin.utils.ProjectPsiFilesUtils.getRelativePath;

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
        StudyNode node = StudyUtils.getStudyNode(myProject, getValue().getVirtualFile());

        String path = getRelativePath(getValue());

        if (node != null && node.getPath().equals(path)) {
            return node.getPosition();
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

    @Override
    public void navigate(boolean requestFocus) {
        VirtualFile virtualFile = getVirtualFile();
        StudyNode studyNode;
        if (virtualFile != null) {
            studyNode = StudyUtils.getStudyNode(myProject, virtualFile);
            StepikProjectManager.setSelected(myProject, studyNode);
        }
    }
}
