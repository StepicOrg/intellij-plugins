package org.stepik.core.projectView.idea

import com.intellij.psi.PsiClass
import org.stepik.core.projectView.StepikTreeStructureProvider


class IdeaTreeStructureProvider : StepikTreeStructureProvider() {
    override fun shouldAdd(any: Any): Boolean {
        return any is PsiClass
    }
}
