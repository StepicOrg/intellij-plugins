package org.stepik.core.ui

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import javafx.scene.web.WebEngine
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.recommendations.ReactionValues.SOLVED
import org.stepik.api.objects.recommendations.ReactionValues.TOO_EASY
import org.stepik.api.objects.recommendations.ReactionValues.TOO_HARD
import org.stepik.api.objects.recommendations.ReactionValues.of
import org.stepik.core.ProjectManager
import org.stepik.core.auth.StepikAuthManager
import org.stepik.core.auth.StepikAuthManager.currentUser
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.getProjectManager
import org.stepik.core.getStudyNode
import org.stepik.core.host
import org.stepik.core.utils.getOrCreateSrcDirectory
import org.stepik.core.utils.navigate
import org.stepik.core.utils.runWriteActionLater
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import java.io.IOException

class LinkListener(val project: Project,
                   private val browser: StudyBrowserWindow,
                   val engine: WebEngine) : EventListener, Loggable {
    
    private val protocolPattern = "([a-z]+):(.*)".toRegex()
    private val pattern = "/lesson(?:/|/[^/]*-)(\\d+)/step/(\\d+).*".toRegex()
    private val projectManager: ProjectManager? = if (!project.isDisposed) getProjectManager(project) else null
    
    override fun handleEvent(ev: Event) {
        if (ev.type == "click") {
            engine.isJavaScriptEnabled = true
            engine.loadWorker.cancel()
            ev.preventDefault()
            val target = ev.target as Element
            var href = getLink(target)
            val matcher = protocolPattern.matchEntire(href)
            if (matcher != null) {
                val protocol = matcher.groupValues[1]
                val link = matcher.groupValues[2]
                
                when (protocol) {
                    "inner"    -> {
                        browseInnerLink(target, link)
                        return
                    }
                    "adaptive" -> {
                        browseAdaptiveLink(link)
                        return
                    }
                }
            }
            
            if (browseProject(href)) {
                return
            }
            
            if (href.startsWith("/")) {
                href = host + href
            }
            
            BrowserUtil.browse(href)
        }
    }
    
    private fun browseInnerLink(target: Element, link: String) {
        val root = projectManager?.projectRoot ?: return
        val stepPath = target.getAttribute("data-step-path")
        val node = getStudyNode(root, stepPath) as? StepNode ?: return
        val contentType = target.getAttribute("data-content-type")
        val prefix = target.getAttribute("data-file-prefix")
        val extension = target.getAttribute("data-file-ext")
        
        val stepikApiClient = StepikAuthManager.stepikApiClient
        val content: String
        try {
            content = stepikApiClient.files()
                    .get(link, contentType)
        } catch (e: StepikClientException) {
            logger.warn(e)
            return
        }
        
        val srcDirectory = getOrCreateSrcDirectory(project, (node as StepNode?)!!, true) ?: return
        getApplication().runWriteActionLater {
            var index = 1
            val filename = "${prefix}_${node.id}"
            var currentFileName = filename + extension
            while (srcDirectory.findChild(currentFileName) != null) {
                currentFileName = "${filename}_${index++}$extension"
            }
            val file = srcDirectory.createChildData(null, currentFileName)
            file.setBinaryContent(content.toByteArray())
            
            try {
                FileEditorManager.getInstance(project)
                        .openFile(file, false)
            } catch (e: IOException) {
                logger.warn(e)
            }
        }
    }
    
    private fun browseAdaptiveLink(link: String) {
        val items = link.split("/")
        if (items.size < 2) {
            return
        }
        
        val reaction = of(items[0])
        if (reaction !in listOf(TOO_EASY, TOO_HARD, SOLVED)) {
            return
        }
        
        browser.showLoadAnimation()
        
        if (reaction in listOf(TOO_EASY, TOO_HARD)) {
            val lessonId: Long
            try {
                lessonId = items[1].toLong()
            } catch (e: NumberFormatException) {
                browser.hideLoadAnimation()
                return
            }
            
            val stepikClient = StepikAuthManager.authAndGetStepikApiClient(true)
            val user = currentUser
            if (user.isGuest) {
                browser.hideLoadAnimation()
                return
            }
            
            stepikClient.recommendationReactions()
                    .post()
                    .user(user.id)
                    .lesson(lessonId)
                    .reaction(reaction)
                    .executeAsync()
                    .whenCompleteAsync { reactions, e ->
                        if (reactions == null) {
                            logger.warn(e)
                        }
                        projectManager?.updateAdaptiveSelected()
                        browser.hideLoadAnimation()
                    }
        } else {
            projectManager?.updateAdaptiveSelected()
            browser.hideLoadAnimation()
        }
    }
    
    private fun browseProject(href: String): Boolean {
        val matcher = pattern.matchEntire(href) ?: return false
        val lessonId = matcher.groupValues[1].toLong()
        val stepPosition = matcher.groupValues[2].toInt()
        
        val root = projectManager?.projectRoot ?: return false
        val lessonNode = root.getChildByClassAndId(LessonNode::class.java, lessonId)
        
        val step = lessonNode?.getChildByPosition(stepPosition) ?: return false
        
        getApplication().invokeLater { navigate(project, step) }
        
        return true
    }
    
    private fun getLink(element: Element): String {
        return element.getAttribute("href") ?: getLinkFromNodeWithCodeTag(element)
    }
    
    private fun getLinkFromNodeWithCodeTag(element: Element): String {
        var parentNode = element.parentNode
        var attributes = parentNode.attributes
        while (attributes.length > 0 && attributes.getNamedItem("class") != null) {
            parentNode = parentNode.parentNode
            attributes = parentNode.attributes
        }
        
        return attributes.getNamedItem("href")?.nodeValue ?: ""
    }
}
