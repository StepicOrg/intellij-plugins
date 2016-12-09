package org.stepik.plugin.projectView;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class IdeaTreeStructureProvider extends StepikTreeStructureProvider {
    @Override
    protected boolean shouldAdd(@NotNull Object object) {
        return object instanceof PsiClass;
    }
}
