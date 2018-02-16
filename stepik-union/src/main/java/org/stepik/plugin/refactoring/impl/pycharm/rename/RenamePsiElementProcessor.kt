package org.stepik.plugin.refactoring.impl.pycharm.rename

import org.stepik.plugin.refactoring.impl.pycharm.AcceptableClasses
import org.stepik.plugin.refactoring.rename.AbstractRenamePsiElementProcessor


class RenamePsiElementProcessor : AbstractRenamePsiElementProcessor() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
