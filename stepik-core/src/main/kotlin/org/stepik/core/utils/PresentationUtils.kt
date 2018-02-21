package org.stepik.core.utils

import com.intellij.ui.JBColor
import org.stepik.core.courseFormat.CourseNode
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.SectionNode
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StepType
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.courseFormat.StudyStatus
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.utils.ProjectFilesUtils.getParent
import org.stepik.core.utils.ProjectFilesUtils.isHideDir
import org.stepik.core.utils.ProjectFilesUtils.isSandbox
import org.stepik.core.utils.ProjectFilesUtils.isStudyItemDir
import org.stepik.core.utils.ProjectFilesUtils.isWithinHideDir
import org.stepik.core.utils.ProjectFilesUtils.isWithinSandbox
import org.stepik.core.utils.ProjectFilesUtils.isWithinSrc
import java.awt.Color
import javax.swing.Icon


object PresentationUtils {

    private val SOLVED_COLOR = JBColor(Color(0, 134, 0), Color(98, 150, 85))
    private val WRONG_COLOR = JBColor(Color(175, 65, 45), Color(175, 75, 60))
    private var icons: Array<Array<Icon>>? = null

    fun getIcon(studyNode: StudyNode, status: StudyStatus?): Icon? {
        if (icons == null) {
            icons = getIcons()
        }

        val set: Array<Icon>
        val clazz = studyNode.javaClass

        set = when (clazz) {
            StepNode::class.java -> when ((studyNode as StepNode).type) {
                StepType.CODE -> icons!![3]
                StepType.TEXT -> icons!![4]
                StepType.VIDEO -> icons!![5]
                else -> icons!![6]
            }
            LessonNode::class.java -> icons!![2]
            SectionNode::class.java -> icons!![1]
            CourseNode::class.java -> icons!![0]
            else -> return null
        }

        return when (status) {
            StudyStatus.SOLVED -> set[1]
            else -> set[0]
        }
    }

    private fun getIcons(): Array<Array<Icon>> {
        return arrayOf(arrayOf(AllStepikIcons.ProjectTree.course, AllStepikIcons.ProjectTree.courseCorrect), arrayOf(AllStepikIcons.ProjectTree.module, AllStepikIcons.ProjectTree.moduleCorrect), arrayOf(AllStepikIcons.ProjectTree.lesson, AllStepikIcons.ProjectTree.lessonCorrect), arrayOf(AllStepikIcons.ProjectTree.stepCode, AllStepikIcons.ProjectTree.stepCodeCorrect), arrayOf(AllStepikIcons.ProjectTree.stepText, AllStepikIcons.ProjectTree.stepTextCorrect), arrayOf(AllStepikIcons.ProjectTree.stepVideo, AllStepikIcons.ProjectTree.stepVideoCorrect), arrayOf(AllStepikIcons.ProjectTree.stepProblem, AllStepikIcons.ProjectTree.stepProblemCorrect))
    }

    fun getColor(status: StudyStatus?): JBColor {
        return when (status) {
            StudyStatus.SOLVED -> return SOLVED_COLOR
            StudyStatus.FAILED -> return WRONG_COLOR
            else -> JBColor.BLACK
        }
    }

    fun isVisibleDirectory(relPath: String): Boolean {
        if (relPath.startsWith("../")) {
            return true
        }

        if (isHideDir(relPath) || isWithinHideDir(relPath)) {
            return false
        }


        return if (isSandbox(relPath) || isStudyItemDir(relPath)) {
            true
        } else isWithinSrc(relPath) || isWithinSandbox(relPath)

    }

    fun isVisibleFile(relFilePath: String): Boolean {
        if (relFilePath.startsWith("../")) {
            return true
        }

        val parentDir = getParent(relFilePath)

        return if (parentDir == null || !isVisibleDirectory(parentDir)) {
            false
        } else isWithinSrc(relFilePath) || isWithinSandbox(relFilePath)
    }
}
