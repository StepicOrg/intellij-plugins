package org.stepik.plugin.refactoring.impl.pycharm.move;

import org.stepik.plugin.refactoring.move.AbstractMoveHandlerDelegate;
import org.stepik.plugin.refactoring.impl.pycharm.AcceptableClasses;

/**
 * @author meanmail
 */
public class MoveHandlerDelegate extends AbstractMoveHandlerDelegate {
    public MoveHandlerDelegate() {
        addAcceptableClasses(AcceptableClasses.get());
    }
}
