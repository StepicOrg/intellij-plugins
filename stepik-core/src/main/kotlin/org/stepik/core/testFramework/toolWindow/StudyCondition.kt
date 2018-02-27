package org.stepik.core.testFramework.toolWindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Condition

class StudyCondition : Condition<Any>, DumbAware {
    override fun value(o: Any) = false
}
