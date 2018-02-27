package org.stepik.core.utils

import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import org.stepik.core.EduNames.HIDE
import org.stepik.core.EduNames.SANDBOX_DIR
import org.stepik.core.EduNames.SRC
import org.stepik.core.StudyUtils.getStudyNode
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.utils.ModuleUtils.createStepModule
import org.stepik.core.utils.ProductGroup.IDEA
import java.io.IOException


const val SEPARATOR = "/"
private const val SEPARATOR_CHAR = '/'
private const val SECTION_EXPR = "(section[0-9]+)"
private const val LESSON_PATH_EXPR = "(section[0-9]+/lesson[0-9]+|lesson[0-9]+)"
private const val STEP_PATH_EXPR = "(section[0-9]+/lesson[0-9]+/step[0-9]+|lesson[0-9]+/step[0-9]+|step[0-9]+)"
private const val SRC_PATH_EXPR = "($STEP_PATH_EXPR$SEPARATOR$SRC|$SRC)"
private const val COURSE_DIRECTORIES = "\\.|$SECTION_EXPR|$LESSON_PATH_EXPR|$STEP_PATH_EXPR|$SRC_PATH_EXPR"
private const val HIDE_PATH_EXPR = "$SRC_PATH_EXPR$SEPARATOR$HIDE"

fun String.isNotTarget(): Boolean {
    return if (this.isHideDir() || this.isWithinHideDir()) {
        true
    } else !(this.isWithinSrc() || this.isWithinSandbox() || this.isSandbox() || this.isSrc())
}

private fun isStepFile(root: StudyNode, path: String): Boolean {
    val studyNode = getStudyNode(root, path)

    studyNode as? StepNode ?: return false

    val filename = studyNode.path.getRelativePath(path)
    return studyNode.isStepFile(filename)
}

fun isNotMovableOrRenameElement(node: StudyNode, path: String): Boolean {
    return if (path.isWithinSrc()) {
        path.isHideDir() || path.isWithinHideDir() || isStepFile(node, path)
    } else !path.isWithinSandbox()

}

fun String.isSandbox(): Boolean {
    return this.matches(SANDBOX_DIR.toRegex())
}

private fun String.isSrc(): Boolean {
    return this.matches(SRC_PATH_EXPR.toRegex())
}

fun String.isWithinSandbox(): Boolean {
    return this.matches(("$SANDBOX_DIR$SEPARATOR.*").toRegex())
}

fun String.isWithinSrc(): Boolean {
    return this.matches(("$SRC_PATH_EXPR$SEPARATOR.*").toRegex())
}

fun String.getRelativePath(path: String): String {
    val relativePath = FileUtil.getRelativePath(this, path, SEPARATOR_CHAR)
    return relativePath ?: path
}

fun String.isStudyItemDir(): Boolean {
    return this.matches(COURSE_DIRECTORIES.toRegex())
}

private fun String.splitPath(): Array<String> {
    return this.split(SEPARATOR).dropLastWhile { it.isEmpty() }.toTypedArray()
}

fun String.isWithinHideDir(): Boolean {
    return this.matches(("$HIDE_PATH_EXPR$SEPARATOR.*").toRegex())
}

fun String.isHideDir(): Boolean {
    return this.matches(HIDE_PATH_EXPR.toRegex())
}

fun getParent(path: String): String? {
    val dirs = path.splitPath()
    if (dirs.isEmpty() || path.isEmpty() || path == ".") {
        return null
    } else if (dirs.size == 1) {
        return "."
    }

    val parentPath = StringBuilder(dirs[0])

    for (i in 1 until dirs.size - 1) {
        parentPath.append(SEPARATOR).append(dirs[i])
    }

    return parentPath.toString()
}

fun getOrCreateSrcDirectory(project: Project, stepNode: StepNode,
                            refresh: Boolean, model: ModifiableModuleModel? = null): VirtualFile? {
    var myModel = model
    val baseDir = project.baseDir
    val srcPath = "${stepNode.path}/$SRC"
    var srcDirectory = baseDir.findFileByRelativePath(srcPath)
    if (srcDirectory == null && !stepNode.wasDeleted) {
        srcDirectory = getOrCreateDirectory(baseDir, srcPath)
        if (srcDirectory != null && IDEA.isCurrent()) {
            val modelOwner = myModel == null
            if (modelOwner) {
                myModel = ModuleManager.getInstance(project).modifiableModel
            }
            getApplication().runWriteActionAndWait {
                createStepModule(project, stepNode, myModel!!)
                if (modelOwner) {
                    myModel.commit()
                }
            }
            if (refresh) {
                VirtualFileManager.getInstance().syncRefresh()
            }
        }
    }
    return srcDirectory
}

internal fun getOrCreateSrcPsiDirectory(project: Project, stepNode: StepNode): PsiDirectory? {
    return getApplication().runReadAction(Computable {
        val directory = getOrCreateSrcDirectory(project, stepNode, true) ?: return@Computable null
        return@Computable PsiManager.getInstance(project).findDirectory(directory)
    })
}

private fun getOrCreateDirectory(baseDir: VirtualFile, directoryPath: String): VirtualFile? {
    return baseDir.findFileByRelativePath(directoryPath)
            ?: getApplication().runWriteActionAndWait {
                return@runWriteActionAndWait directoryPath.splitPath()
                        .fold(baseDir) { dir, part ->
                            try {
                                return@fold dir.findChild(part) ?: dir.createChildDirectory(null, part)
                            } catch (e: IOException) {
                                return@runWriteActionAndWait null
                            }
                        }
            }
}

internal fun getOrCreatePsiDirectory(project: Project, baseDir: PsiDirectory,
                                     relativePath: String): PsiDirectory? {
    val directory = getOrCreateDirectory(baseDir.virtualFile, relativePath) ?: return null

    return getApplication().runReadAction(Computable {
        PsiManager.getInstance(project).findDirectory(directory)
    })
}
