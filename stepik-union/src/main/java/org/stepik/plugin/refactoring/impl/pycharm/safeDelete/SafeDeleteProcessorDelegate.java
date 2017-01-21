package org.stepik.plugin.refactoring.impl.pycharm.safeDelete;

import org.stepik.plugin.refactoring.impl.pycharm.AcceptableClasses;
import org.stepik.plugin.refactoring.safeDelete.AbstractSafeDeleteProcessorDelegate;

/**
 * @author meanmail
 */
public class SafeDeleteProcessorDelegate extends AbstractSafeDeleteProcessorDelegate {
    public SafeDeleteProcessorDelegate() {
        addAcceptableClasses(AcceptableClasses.get());
    }
}
