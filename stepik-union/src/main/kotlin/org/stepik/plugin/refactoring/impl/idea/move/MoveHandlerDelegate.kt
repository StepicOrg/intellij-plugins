package org.stepik.plugin.refactoring.impl.idea.move

import org.stepik.plugin.refactoring.impl.idea.AcceptableClasses
import org.stepik.plugin.refactoring.move.AbstractMoveHandlerDelegate


class MoveHandlerDelegate : AbstractMoveHandlerDelegate() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
