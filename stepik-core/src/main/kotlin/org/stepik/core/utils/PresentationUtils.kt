package org.stepik.core.utils

import com.intellij.ui.JBColor
import org.stepik.core.courseFormat.CourseNode
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.SectionNode
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StepType.CODE
import org.stepik.core.courseFormat.StepType.TEXT
import org.stepik.core.courseFormat.StepType.VIDEO
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.courseFormat.StudyStatus
import org.stepik.core.courseFormat.StudyStatus.FAILED
import org.stepik.core.courseFormat.StudyStatus.SOLVED
import org.stepik.core.icons.AllStepikIcons.ProjectTree.course
import org.stepik.core.icons.AllStepikIcons.ProjectTree.courseCorrect
import org.stepik.core.icons.AllStepikIcons.ProjectTree.lesson
import org.stepik.core.icons.AllStepikIcons.ProjectTree.lessonCorrect
import org.stepik.core.icons.AllStepikIcons.ProjectTree.module
import org.stepik.core.icons.AllStepikIcons.ProjectTree.moduleCorrect
import org.stepik.core.icons.AllStepikIcons.ProjectTree.stepCode
import org.stepik.core.icons.AllStepikIcons.ProjectTree.stepCodeCorrect
import org.stepik.core.icons.AllStepikIcons.ProjectTree.stepProblem
import org.stepik.core.icons.AllStepikIcons.ProjectTree.stepProblemCorrect
import org.stepik.core.icons.AllStepikIcons.ProjectTree.stepText
import org.stepik.core.icons.AllStepikIcons.ProjectTree.stepTextCorrect
import org.stepik.core.icons.AllStepikIcons.ProjectTree.stepVideo
import org.stepik.core.icons.AllStepikIcons.ProjectTree.stepVideoCorrect
import java.awt.Color
import javax.swing.Icon


private val SOLVED_COLOR = JBColor(Color(0, 134, 0), Color(98, 150, 85))
private val WRONG_COLOR = JBColor(Color(175, 65, 45), Color(175, 75, 60))
private val icons by lazy {
    arrayOf(
            arrayOf(course, courseCorrect),
            arrayOf(module, moduleCorrect),
            arrayOf(lesson, lessonCorrect),
            arrayOf(stepCode, stepCodeCorrect),
            arrayOf(stepText, stepTextCorrect),
            arrayOf(stepVideo, stepVideoCorrect),
            arrayOf(stepProblem, stepProblemCorrect)
    )
}

fun StudyNode.getIcon(): Icon? {
    val setIndex = when (this.javaClass) {
        StepNode::class.java -> {
            this as StepNode
            when (type) {
                CODE -> 3
                TEXT -> 4
                VIDEO -> 5
                else -> 6
            }
        }
        LessonNode::class.java -> 2
        SectionNode::class.java -> 1
        CourseNode::class.java -> 0
        else -> return null
    }

    val set = icons[setIndex]

    return when (status) {
        SOLVED -> set[1]
        else -> set[0]
    }
}

fun StudyStatus?.getColor(): JBColor {
    return when (this) {
        SOLVED -> return SOLVED_COLOR
        FAILED -> return WRONG_COLOR
        else -> JBColor.BLACK
    }
}

fun String.isVisibleDirectory(): Boolean {
    if (startsWith("../")) {
        return true
    }

    if (isHideDir() || isWithinHideDir()) {
        return false
    }

    return if (isSandbox() || isStudyItemDir()) {
        true
    } else isWithinSrc() || isWithinSandbox()

}

fun String.isVisibleFile(): Boolean {
    if (startsWith("../")) {
        return true
    }

    val parentDir = getParent(this)

    return if (parentDir == null || !parentDir.isVisibleDirectory()) {
        false
    } else isWithinSrc() || isWithinSandbox()
}
