package org.stepik.plugin.refactoring.impl.idea.move;

import org.stepik.plugin.refactoring.impl.idea.AcceptableClasses;
import org.stepik.plugin.refactoring.move.AbstractMoveHandlerDelegate;

/**
 * @author meanmail
 */
public class MoveHandlerDelegate extends AbstractMoveHandlerDelegate {
    public MoveHandlerDelegate() {
        addAcceptableClasses(AcceptableClasses.get());
    }
}
