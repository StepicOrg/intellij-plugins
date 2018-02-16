package org.stepik.plugin.refactoring.impl.pycharm.move

import org.stepik.plugin.refactoring.impl.pycharm.AcceptableClasses
import org.stepik.plugin.refactoring.move.AbstractMoveHandlerDelegate


class MoveHandlerDelegate : AbstractMoveHandlerDelegate() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
