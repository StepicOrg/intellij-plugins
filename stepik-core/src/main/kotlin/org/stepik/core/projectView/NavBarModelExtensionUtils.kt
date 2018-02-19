package org.stepik.core.projectView

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.stepik.core.ProjectManager
import org.stepik.core.utils.PresentationDataUtils.isVisibleDirectory
import org.stepik.core.utils.PresentationDataUtils.isVisibleFile
import org.stepik.core.utils.PresentationDataUtils.updatePresentationData


object NavBarModelExtensionUtils {
    fun getPresentableText(any: Any?): String? {
        if (any is Project) {
            val projectManager = getService(any, ProjectManager::class.java)
            return projectManager.projectRoot?.name
        }

        if (any is PsiDirectory) {
            val data = PresentationData()
            updatePresentationData(data, any)
            return data.presentableText
        }

        return null
    }

    fun adjustElement(psiElement: PsiElement): PsiElement? {
        val projectManager = getService(psiElement.project, ProjectManager::class.java)
        projectManager?.projectRoot ?: return psiElement

        when (psiElement) {
            is PsiDirectory -> if (!isVisibleDirectory(psiElement)) return null
            is PsiFile -> if (!isVisibleFile(psiElement)) return null
        }

        return psiElement
    }
}
