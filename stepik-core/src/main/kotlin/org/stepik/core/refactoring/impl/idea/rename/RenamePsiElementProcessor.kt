package org.stepik.core.refactoring.impl.idea.rename

import org.stepik.core.refactoring.impl.idea.AcceptableClasses
import org.stepik.core.refactoring.rename.AbstractRenamePsiElementProcessor


class RenamePsiElementProcessor : AbstractRenamePsiElementProcessor() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
