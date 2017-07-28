package org.stepik.core.testFramework.runners

import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiClassUtil
import com.intellij.psi.util.PsiMethodUtil
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.processes.JavaProcess
import org.stepik.core.testFramework.processes.TestProcess

class JavaRunner : JetRunner() {
    override fun getTypeName(): String = "Application"

    override fun getFactoryName(): String = "Application"

    override fun setWorkingDirectory(appConfiguration: RunConfiguration,
                                     workingVirtualDirectory: VirtualFile) {
        val workingDirectory = workingVirtualDirectory.path
        (appConfiguration as ApplicationConfiguration).workingDirectory = workingDirectory
    }

    override fun setMainClass(application: Application,
                              project: Project,
                              appConfiguration: RunConfiguration,
                              mainVirtualFile: VirtualFile?) {
        if (mainVirtualFile != null) {
            val mainPsiFile = Array<Any?>(1, { null })
            application.invokeAndWait {
                val psiManager = PsiManager.getInstance(project)
                mainPsiFile[0] = psiManager.findFile(mainVirtualFile)
            }
            val mainPsiClass = mainPsiFile[0]
            if (mainPsiClass is PsiJavaFile) {
                DumbService.getInstance(project).runReadActionInSmartMode {
                    val mainClass = mainPsiClass.classes
                            .filter {
                                PsiClassUtil.isRunnableClass(it, false) && PsiMethodUtil.hasMainMethod(it)
                            }
                            .getOrNull(0)
                            ?: return@runReadActionInSmartMode
                    (appConfiguration as ApplicationConfiguration).mainClass = mainClass
                }
            }
        }
    }

    override fun createTestProcess(project: Project, stepNode: StepNode, mainFilePath: String): TestProcess {
        return JavaProcess(project, stepNode, mainFilePath)
    }
}
