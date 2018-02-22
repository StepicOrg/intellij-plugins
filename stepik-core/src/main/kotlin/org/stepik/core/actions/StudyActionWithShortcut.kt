package org.stepik.core.actions

import com.intellij.openapi.project.DumbAwareAction
import org.stepik.core.common.Loggable
import javax.swing.Icon

abstract class StudyActionWithShortcut protected constructor(text: String?,
                                                             description: String?,
                                                             icon: Icon? = null) :
        DumbAwareAction(text, description, icon), Loggable {
    abstract fun getActionId(): String
    abstract fun getShortcuts(): Array<String>?
}
