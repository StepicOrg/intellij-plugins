package org.stepik.plugin.projectView.idea;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.projectView.StepikTreeStructureProvider;

/**
 * @author meanmail
 */
public class IdeaTreeStructureProvider extends StepikTreeStructureProvider {
    @Override
    protected boolean shouldAdd(@NotNull Object object) {
        return object instanceof PsiClass;
    }
}
