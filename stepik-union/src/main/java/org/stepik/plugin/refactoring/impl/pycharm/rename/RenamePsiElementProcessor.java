package org.stepik.plugin.refactoring.impl.pycharm.rename;

import org.stepik.plugin.refactoring.impl.pycharm.AcceptableClasses;
import org.stepik.plugin.refactoring.rename.AbstractRenamePsiElementProcessor;

/**
 * @author meanmail
 */
public class RenamePsiElementProcessor extends AbstractRenamePsiElementProcessor {
    public RenamePsiElementProcessor() {
        addAcceptableClasses(AcceptableClasses.get());
    }
}
