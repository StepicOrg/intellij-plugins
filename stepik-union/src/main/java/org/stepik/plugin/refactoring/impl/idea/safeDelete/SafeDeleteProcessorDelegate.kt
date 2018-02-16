package org.stepik.plugin.refactoring.impl.idea.safeDelete

import org.stepik.plugin.refactoring.impl.idea.AcceptableClasses
import org.stepik.plugin.refactoring.safeDelete.AbstractSafeDeleteProcessorDelegate


class SafeDeleteProcessorDelegate : AbstractSafeDeleteProcessorDelegate() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
