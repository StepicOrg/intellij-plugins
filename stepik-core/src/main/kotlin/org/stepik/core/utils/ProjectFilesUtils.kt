package org.stepik.core.utils

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import org.stepik.core.EduNames
import org.stepik.core.StudyUtils
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.utils.ProductGroup.IDEA
import java.io.IOException


object ProjectFilesUtils {

    const val SEPARATOR = "/"
    private const val SEPARATOR_CHAR = '/'
    private const val SECTION_EXPR = "(section[0-9]+)"
    private const val LESSON_PATH_EXPR = "(section[0-9]+/lesson[0-9]+|lesson[0-9]+)"
    private const val STEP_PATH_EXPR = "(section[0-9]+/lesson[0-9]+/step[0-9]+|lesson[0-9]+/step[0-9]+|step[0-9]+)"
    private const val SRC_PATH_EXPR = "(" + STEP_PATH_EXPR + SEPARATOR + EduNames.SRC + "|" + EduNames.SRC + ")"
    private const val COURSE_DIRECTORIES = "\\.|$SECTION_EXPR|$LESSON_PATH_EXPR|$STEP_PATH_EXPR|$SRC_PATH_EXPR"
    private const val HIDE_PATH_EXPR = SRC_PATH_EXPR + SEPARATOR + EduNames.HIDE

    fun isCanNotBeTarget(targetPath: String): Boolean {

        return if (isHideDir(targetPath) || isWithinHideDir(targetPath)) {
            true
        } else !(isWithinSrc(targetPath) || isWithinSandbox(targetPath) || isSandbox(targetPath) || isSrc(targetPath))
    }

    private fun isStepFile(root: StudyNode, path: String): Boolean {
        val studyNode = StudyUtils.getStudyNode(root, path)

        if (studyNode is StepNode) {
            val filename = getRelativePath(studyNode.path, path)
            return studyNode.isStepFile(filename)
        }
        return false
    }

    fun isNotMovableOrRenameElement(node: StudyNode, path: String): Boolean {
        return if (isWithinSrc(path)) {
            isHideDir(path) || isWithinHideDir(path) || isStepFile(node, path)
        } else !isWithinSandbox(path)

    }

    fun isSandbox(path: String): Boolean {
        return path.matches(EduNames.SANDBOX_DIR.toRegex())
    }

    private fun isSrc(path: String): Boolean {
        return path.matches(SRC_PATH_EXPR.toRegex())
    }

    fun isWithinSandbox(path: String): Boolean {
        return path.matches((EduNames.SANDBOX_DIR + SEPARATOR + ".*").toRegex())
    }

    fun isWithinSrc(path: String): Boolean {
        return path.matches((SRC_PATH_EXPR + SEPARATOR + ".*").toRegex())
    }

    fun getRelativePath(basePath: String, path: String): String {
        val relativePath = FileUtil.getRelativePath(basePath, path, SEPARATOR_CHAR)
        return relativePath ?: path
    }

    fun isStudyItemDir(relativePath: String): Boolean {
        return relativePath.matches(COURSE_DIRECTORIES.toRegex())
    }

    private fun splitPath(path: String): Array<String> {
        return path.split(SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    fun isWithinHideDir(path: String): Boolean {
        return path.matches((HIDE_PATH_EXPR + SEPARATOR + ".*").toRegex())
    }

    fun isHideDir(path: String): Boolean {
        return path.matches(HIDE_PATH_EXPR.toRegex())
    }

    fun getParent(path: String): String? {
        val dirs = splitPath(path)
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


    @JvmOverloads
    fun getOrCreateSrcDirectory(
            project: Project,
            stepNode: StepNode,
            refresh: Boolean,
            model: ModifiableModuleModel? = null): VirtualFile? {
        var myModel = model
        val baseDir = project.baseDir
        val srcPath = "${stepNode.path}/${EduNames.SRC}"
        var srcDirectory = baseDir.findFileByRelativePath(srcPath)
        if (srcDirectory == null && !stepNode.wasDeleted) {
            srcDirectory = getOrCreateDirectory(baseDir, srcPath)
            if (srcDirectory != null && PluginUtils.isCurrent(IDEA)) {
                val modelOwner = myModel == null
                if (modelOwner) {
                    myModel = ModuleManager.getInstance(project).modifiableModel
                }
                val application = ApplicationManager.getApplication()
                application.invokeAndWait {
                    application.runWriteAction {
                        ModuleUtils.createStepModule(project, stepNode, myModel!!)
                        if (modelOwner) {
                            myModel.commit()
                        }
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
        val application = ApplicationManager.getApplication()
        return application.runReadAction(Computable {
            val directory = getOrCreateSrcDirectory(project, stepNode, true) ?: return@Computable null
            return@Computable PsiManager.getInstance(project).findDirectory(directory)
        })
    }

    private fun getOrCreateDirectory(baseDir: VirtualFile, directoryPath: String): VirtualFile? {
        var srcDir = baseDir.findFileByRelativePath(directoryPath)
        if (srcDir == null) {
            val application = ApplicationManager.getApplication()
            application.invokeAndWait {
                srcDir = application.runWriteAction(Computable {
                    var dir: VirtualFile
                    try {
                        val paths = directoryPath.split("/").dropLastWhile { it.isEmpty() }
                        dir = baseDir
                        for (path in paths) {
                            val child = dir.findChild(path)
                            dir = child ?: dir.createChildDirectory(null, path)
                        }
                    } catch (e: IOException) {
                        return@Computable null
                    }

                    return@Computable dir
                })
            }
        }
        return srcDir
    }

    internal fun getOrCreatePsiDirectory(
            project: Project,
            baseDir: PsiDirectory,
            relativePath: String): PsiDirectory? {
        val directory = getOrCreateDirectory(baseDir.virtualFile, relativePath) ?: return null

        val application = ApplicationManager.getApplication()
        return application.runReadAction(Computable {
            PsiManager.getInstance(project).findDirectory(directory)
        })
    }
}
