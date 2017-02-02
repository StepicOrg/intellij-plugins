package org.stepik.plugin.refactoring.impl.idea.safeDelete;

import org.stepik.plugin.refactoring.impl.idea.AcceptableClasses;
import org.stepik.plugin.refactoring.safeDelete.AbstractSafeDeleteProcessorDelegate;

/**
 * @author meanmail
 */
public class SafeDeleteProcessorDelegate extends AbstractSafeDeleteProcessorDelegate {
    public SafeDeleteProcessorDelegate() {
        addAcceptableClasses(AcceptableClasses.get());
    }
}
