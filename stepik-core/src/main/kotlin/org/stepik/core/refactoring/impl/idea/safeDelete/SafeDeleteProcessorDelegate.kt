package org.stepik.core.refactoring.impl.idea.safeDelete

import org.stepik.core.refactoring.impl.idea.AcceptableClasses
import org.stepik.core.refactoring.safeDelete.AbstractSafeDeleteProcessorDelegate


class SafeDeleteProcessorDelegate : AbstractSafeDeleteProcessorDelegate() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
