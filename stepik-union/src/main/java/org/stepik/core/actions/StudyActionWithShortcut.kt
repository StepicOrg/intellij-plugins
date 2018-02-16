package org.stepik.core.actions

import com.intellij.openapi.project.DumbAwareAction
import javax.swing.Icon

abstract class StudyActionWithShortcut protected constructor(text: String?,
                                                             description: String?,
                                                             icon: Icon?) : DumbAwareAction(text, description, icon) {
    abstract fun getActionId(): String
    abstract fun getShortcuts(): Array<String>?
}
