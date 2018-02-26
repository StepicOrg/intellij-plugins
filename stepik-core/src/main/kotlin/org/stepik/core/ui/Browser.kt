package org.stepik.core.ui

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo
import com.intellij.ide.util.PropertiesComponent
import com.sun.javafx.webkit.WebConsoleListener
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import netscape.javascript.JSObject
import org.intellij.lang.annotations.Language
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.stepHelpers.VideoTheoryHelper.Companion.VIDEO_QUALITY_PROPERTY_NAME
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.WindowConstants


open class Browser internal constructor() : JFrame(), Loggable {
    private val bridge = JavaBridge
    internal val panel: JFXPanel = JFXPanel()
    private var webComponent: WebView? = null
    private var pane: BorderPane? = null
    var engine: WebEngine? = null

    init {
        size = Dimension(900, 800)
        layout = BorderLayout()
        title = "Study Browser"
        LafManager.getInstance().addLafManagerListener(StudyLafManagerListener())
        initComponents()

        WebConsoleListener.setDefaultListener { _, message, lineNumber, sourceId ->
            logger.warn("Console: [$sourceId:$lineNumber] $message")
        }
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
            pane = BorderPane()
            webComponent = WebView()
            engine = webComponent!!.engine
            pane!!.center = webComponent
            initConsoleListener()
            val scene = Scene(pane!!)
            panel.scene = scene
            panel.isVisible = true
            updateLaf(LafManager.getInstance().currentLookAndFeel is DarculaLookAndFeelInfo)
            afterInitComponents()
        }

        add(panel, BorderLayout.CENTER)
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    }

    protected open fun afterInitComponents() {
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
                        };"""
                    engine!!.executeScript(script)
                }
    }


    protected fun getExternalURL(internalPath: String): String {
        return javaClass.getResource(internalPath).toExternalForm()
    }

    private inner class StudyLafManagerListener : LafManagerListener {
        override fun lookAndFeelChanged(manager: LafManager) {
            updateLaf(manager.currentLookAndFeel is DarculaLookAndFeelInfo)
        }
    }

    object JavaBridge : Loggable {
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

        fun setVideoQuality(value: Int?) {
            PropertiesComponent.getInstance()
                    .setValue(VIDEO_QUALITY_PROPERTY_NAME, value.toString())
        }
    }
}
