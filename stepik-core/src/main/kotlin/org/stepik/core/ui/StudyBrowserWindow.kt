package org.stepik.core.ui

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import netscape.javascript.JSException
import netscape.javascript.JSObject
import org.intellij.lang.annotations.Language
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.stepHelpers.VideoTheoryHelper
import org.stepik.core.templates.Templater
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLInputElement
import java.awt.BorderLayout
import java.awt.Dimension
import java.nio.charset.Charset
import java.util.*
import javax.swing.JFrame
import javax.swing.WindowConstants

class StudyBrowserWindow internal constructor(private val project: Project) : JFrame(), Loggable {
    private val bridge = JavaBridge()
    val panel: JFXPanel = JFXPanel()
    private var webComponent: WebView? = null
    private var pane: StackPane? = null
    private var engine: WebEngine? = null

    init {
        size = Dimension(900, 800)
        layout = BorderLayout()
        title = "Study Browser"
        LafManager.getInstance().addLafManagerListener(StudyLafManagerListener())
        initComponents()
    }

    private fun updateLaf(isDarcula: Boolean) {
        if (isDarcula) {
            updateLafDarcula()
        } else {
            updateIntellijAndGTKLaf()
        }
    }

    private fun updateIntellijAndGTKLaf() {
        Platform.runLater {
            val scrollBarStyleUrl = getExternalURL("/style/javaFXBrowserScrollBar.css")
            pane!!.stylesheets.add(scrollBarStyleUrl)
            engine!!.userStyleSheetLocation = null
            engine!!.reload()
        }
    }

    private fun updateLafDarcula() {
        Platform.runLater {
            val engineStyleUrl = getExternalURL("/style/javaFXBrowserDarcula.css")
            val scrollBarStyleUrl = getExternalURL("/style/javaFXBrowserDarculaScrollBar.css")
            engine!!.userStyleSheetLocation = engineStyleUrl
            pane!!.stylesheets.add(scrollBarStyleUrl)
            pane!!.style = "-fx-background-color: #3c3f41"
            panel.scene.stylesheets.add(engineStyleUrl)
            engine!!.reload()
        }
    }

    private fun initComponents() {
        Platform.runLater {
            pane = StackPane()
            webComponent = WebView()
            engine = webComponent!!.engine
            pane!!.children.add(webComponent)
            initHyperlinkListener()
            initConsoleListener()
            val scene = Scene(pane!!)
            panel.scene = scene
            panel.isVisible = true
            updateLaf(LafManager.getInstance().currentLookAndFeel is DarculaLookAndFeelInfo)
        }

        add(panel, BorderLayout.CENTER)
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    }

    private fun initConsoleListener() {
        engine!!.loadWorker
                .stateProperty()
                .addListener { _, _, newValue ->
                    if (newValue != Worker.State.SUCCEEDED) {
                        return@addListener
                    }
                    val window = engine!!.executeScript("window") as JSObject
                    window.setMember("java", bridge)
                    @Language("JavaScript")
                    val script = """
                        console.error = function (message) {
                            java.error(message);
                        };
                        console.warn = function (message) {
                            java.warn(message);
                        };
                        console.log = function (message) {
                            java.log(message);
                        };
                        console.debug = function (message) {
                            java.debug(message);
                        };
                        window.addEventListener('error', function (e) {
                            java.doError(e.filename, e.lineno, e.colno, e.message);
                            return true;
                        });"""
                    engine!!.executeScript(script)
                }
    }

    fun loadContent(template: String, context: Map<String, Any>) {
        val content = getContent(template, context)
        Platform.runLater {
            val document = engine!!.document
            if (document != null) {
                val form = document.getElementById("answer_form") as? HTMLFormElement
                if (form != null) {
                    val action = form.elements.namedItem("action") as HTMLInputElement
                    action.value = "save_reply"
                    FormListener.handle(project, this, form)
                }
            }
            engine!!.loadContent(content)
        }
    }

    private fun getContent(template: String, context: Map<String, Any>): String {
        val editorColorsScheme = EditorColorsManager.getInstance().globalScheme
        val fontSize = editorColorsScheme.editorFontSize

        val map = HashMap<String, Any>()
        map["font_size"] = (fontSize - 2).toString()
        map["highlight"] = getExternalURL("/highlight/highlight.pack.js")
        if (LafManager.getInstance().currentLookAndFeel is DarculaLookAndFeelInfo) {
            map["css_highlight"] = getExternalURL("/highlight/styles/darcula.css")
        } else {
            map["css_highlight"] = getExternalURL("/highlight/styles/idea.css")
        }
        map["charset"] = Charset.defaultCharset().displayName()
        map["loader"] = getExternalURL("/templates/img/loader.svg")
        map["login_css"] = getExternalURL("/templates/login/css/login.css")
        map.putAll(context)

        return Templater.processTemplate(template, map)
    }

    private fun getExternalURL(internalPath: String): String {
        return javaClass.getResource(internalPath).toExternalForm()
    }

    private fun initHyperlinkListener() {
        engine!!.loadWorker.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                val linkListener = makeHyperLinkListener()
                addListenerToAllHyperlinkItems(linkListener)

                val formListener = FormListener(project, this)
                val doc = engine!!.document
                (doc as EventTarget).addEventListener(FormListener.EVENT_TYPE_SUBMIT, formListener, false)
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

    private inner class StudyLafManagerListener : LafManagerListener {
        override fun lookAndFeelChanged(manager: LafManager) {
            updateLaf(manager.currentLookAndFeel is DarculaLookAndFeelInfo)
        }
    }

    inner class JavaBridge {
        fun log(text: String) {
            logger.info("console: $text")
        }

        fun error(text: String) {
            logger.error("console: $text")
        }

        fun warn(text: String) {
            logger.warn("console: $text")
        }

        fun debug(text: String) {
            logger.debug("console: $text")
        }

        fun doError(filename: String, lineno: Int, colno: Int, message: String) {
            error("\nfilename: $filename\nline: $lineno\ncolumn: $colno\nmessage: $message")
        }

        fun setVideoQuality(value: Int?) {
            PropertiesComponent.getInstance()
                    .setValue(VideoTheoryHelper.VIDEO_QUALITY_PROPERTY_NAME, value.toString())
        }
    }

    companion object {
        private const val EVENT_TYPE_CLICK = "click"
    }
}
