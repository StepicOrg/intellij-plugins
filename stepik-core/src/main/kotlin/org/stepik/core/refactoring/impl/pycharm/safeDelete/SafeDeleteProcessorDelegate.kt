package org.stepik.core.refactoring.impl.pycharm.safeDelete

import org.stepik.core.refactoring.impl.pycharm.AcceptableClasses
import org.stepik.core.refactoring.safeDelete.AbstractSafeDeleteProcessorDelegate


class SafeDeleteProcessorDelegate : AbstractSafeDeleteProcessorDelegate() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
