package org.stepik.core.refactoring.impl.idea.move

import org.stepik.core.refactoring.impl.idea.AcceptableClasses
import org.stepik.core.refactoring.move.AbstractMoveHandlerDelegate


class MoveHandlerDelegate : AbstractMoveHandlerDelegate() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
