package org.stepik.plugin.refactoring.impl.idea.rename

import org.stepik.plugin.refactoring.impl.idea.AcceptableClasses
import org.stepik.plugin.refactoring.rename.AbstractRenamePsiElementProcessor


class RenamePsiElementProcessor : AbstractRenamePsiElementProcessor() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
