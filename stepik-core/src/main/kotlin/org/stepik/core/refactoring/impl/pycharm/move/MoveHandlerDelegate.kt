package org.stepik.core.refactoring.impl.pycharm.move

import org.stepik.core.refactoring.impl.pycharm.AcceptableClasses
import org.stepik.core.refactoring.move.AbstractMoveHandlerDelegate


class MoveHandlerDelegate : AbstractMoveHandlerDelegate() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
