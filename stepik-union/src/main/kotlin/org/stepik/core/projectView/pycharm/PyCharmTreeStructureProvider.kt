package org.stepik.core.projectView.pycharm

import org.stepik.core.projectView.StepikTreeStructureProvider


class PyCharmTreeStructureProvider : StepikTreeStructureProvider() {
    override fun shouldAdd(any: Any) = false
}
