package org.stepik.core.ui

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import javafx.application.Platform
import javafx.concurrent.Worker
import netscape.javascript.JSException
import org.stepik.core.common.Loggable
import org.stepik.core.templates.Templater
import org.stepik.core.ui.FormListener.Companion.EVENT_TYPE_SUBMIT
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLInputElement
import java.nio.charset.Charset

class StudyBrowserWindow internal constructor(private val project: Project) : Browser(), Loggable {

    override fun afterInitComponents() {
        initListeners()
    }

    fun loadContent(template: String, context: Map<String, Any>, init: () -> Unit = {}) {
        val content = getContent(template, context)
        Platform.runLater {
            val form = engine!!.document?.getElementById("answer_form") as? HTMLFormElement
            if (form != null) {
                val action = form.elements.namedItem("action") as? HTMLInputElement
                action?.value = "save_reply"
                FormListener.handle(project, this, form)
            }
            engine!!.loadContent(content)
            engine!!.loadWorker
                    .stateProperty()
                    .addListener { _, _, newValue ->
                        if (newValue == Worker.State.SUCCEEDED) {
                            init()
                        }
                    }
        }
    }

    private fun getContent(template: String, context: Map<String, Any>): String {
        val editorColorsScheme = EditorColorsManager.getInstance().globalScheme
        val fontSize = editorColorsScheme.editorFontSize

        val map = mutableMapOf<String, Any>(
                "font_size" to (fontSize - 2).toString(),
                "highlight" to getExternalURL("/highlight/highlight.pack.js"),
                "css_highlight" to getExternalURL(
                        if (LafManager.getInstance().currentLookAndFeel is DarculaLookAndFeelInfo) {
                            "/highlight/styles/darcula.css"
                        } else {
                            "/highlight/styles/idea.css"
                        }
                ),
                "charset" to Charset.defaultCharset().displayName(),
                "loader" to getExternalURL("/templates/img/loader.svg"),
                "login_css" to getExternalURL("/templates/login/css/login.css")
        )
        map.putAll(context)

        return Templater.processTemplate(template, map)
    }

    private fun initListeners() {
        engine!!.loadWorker.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                val linkListener = makeHyperLinkListener()
                addListenerToAllHyperlinkItems(linkListener)

                val formListener = FormListener(project, this)
                val doc = engine!!.document
                (doc as EventTarget).addEventListener(EVENT_TYPE_SUBMIT, formListener, false)
            }
        }
    }

    private fun addListenerToAllHyperlinkItems(listener: EventListener) {
        val doc = engine!!.document
        if (doc != null) {
            val nodeList = doc.getElementsByTagName("a")
            for (i in 0 until nodeList.length) {
                (nodeList.item(i) as EventTarget).addEventListener(EVENT_TYPE_CLICK, listener, false)
            }
        }
    }

    private fun makeHyperLinkListener(): EventListener {
        return LinkListener(project, this, engine!!)
    }

    fun showLoadAnimation() {
        callFunction("showLoadAnimation")
    }

    fun hideLoadAnimation() {
        callFunction("hideLoadAnimation")
    }

    internal fun callFunction(name: String, vararg args: String) {
        Platform.runLater {
            try {
                val argsString = args.joinToString(",") { "\"$it\"" }
                val script = "if (window.$name !== undefined) $name($argsString);"
                engine!!.executeScript(script)
            } catch (e: JSException) {
                logger.error(e)
            }
        }
    }

    companion object {
        private const val EVENT_TYPE_CLICK = "click"
    }
}
