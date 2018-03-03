package org.stepik.core.refactoring.impl.pycharm.rename

import org.stepik.core.refactoring.impl.pycharm.AcceptableClasses
import org.stepik.core.refactoring.rename.AbstractRenamePsiElementProcessor


class RenamePsiElementProcessor : AbstractRenamePsiElementProcessor() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
