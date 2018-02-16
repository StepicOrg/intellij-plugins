package org.stepik.plugin.refactoring.impl.pycharm.safeDelete

import org.stepik.plugin.refactoring.impl.pycharm.AcceptableClasses
import org.stepik.plugin.refactoring.safeDelete.AbstractSafeDeleteProcessorDelegate


class SafeDeleteProcessorDelegate : AbstractSafeDeleteProcessorDelegate() {
    init {
        addAcceptableClasses(AcceptableClasses.get())
    }
}
