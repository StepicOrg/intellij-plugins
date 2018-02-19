package org.stepik.core.auth.ui

import com.intellij.icons.AllIcons
import com.intellij.util.ui.UIUtil
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import org.stepik.api.urls.Urls
import org.stepik.core.auth.webkit.network.CookieManager
import org.stepik.core.stepik.StepikAuthManager
import org.stepik.core.templates.Templater
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Frame
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.Icon
import javax.swing.JDialog
import javax.swing.WindowConstants


class AuthDialog private constructor() : JDialog(null as Frame?, true) {
    private val map = HashMap<String, String>()
    private var clearedCookies: Boolean = false
    private val cookieManager = initCookieManager(false)
    private var url: String? = null
    private var engine: WebEngine? = null
    private var progressBar: Node? = null
    private val panel: JFXPanel

    init {
        title = "Authorize"
        size = Dimension(640, 480)
        setLocationRelativeTo(null)
        layout = BorderLayout()
        panel = JFXPanel()
        Platform.setImplicitExit(false)
        Platform.runLater {
            val backButton = makeGoBackButton()
            addButtonsAvailabilityListeners(backButton)
            progressBar = makeProgressBarWithListener()

            val toolPane = HBox().apply {
                spacing = 5.0
                alignment = Pos.CENTER_LEFT
                children.addAll(backButton, makeHomeButton(), makeExitButton(), progressBar)
                padding = Insets(5.0)
            }

            val webComponent = WebView()

            val pane = BorderPane().apply {
                top = toolPane
                center = webComponent
            }

            panel.apply {
                scene = Scene(pane)
                isVisible = true
            }

            engine = webComponent.engine
            engine!!.userAgent = StepikAuthManager.userAgent
            url = StepikAuthManager.implicitGrantUrl
            engine!!.load(url)
        }
        add(panel, BorderLayout.CENTER)
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    }

    private fun initCookieManager(clearCookies: Boolean): CookieManager {
        val cookieManager = CookieManager.getDefault() as? CookieManager ?: CookieManager()

        if (clearCookies) {
            cookieManager.clear()
            clearedCookies = true
        }

        CookieManager.setDefault(cookieManager)
        return cookieManager
    }

    private fun saveCookies() {
        cookieManager.save()
    }

    private fun addButtonsAvailabilityListeners(goBackButton: Button) {
        Platform.runLater {
            engine!!.loadWorker.stateProperty().addListener { _, _, newState ->
                if (newState == Worker.State.SUCCEEDED) {
                    val history = engine!!.history
                    val isGoBackDisable = history.currentIndex <= 0
                    goBackButton.isDisable = isGoBackDisable
                }
            }
        }
    }

    private fun makeGoBackButton(): Button {
        return createButtonWithImage(AllIcons.Actions.Back).apply {
            tooltip = Tooltip("Back")
            isDisable = true
            setOnAction { Platform.runLater { engine!!.history.go(-1) } }
        }
    }

    private fun makeHomeButton(): Button {
        return createButtonWithImage(AllIcons.Actions.Refresh).apply {
            tooltip = Tooltip("To home")
            setOnAction { Platform.runLater { engine!!.load(url) } }
        }
    }

    private fun makeExitButton(): Button {
        return createButtonWithImage(AllIcons.Actions.Exit).apply {
            tooltip = Tooltip("Login to another account")
            setOnAction {
                Platform.runLater {
                    initCookieManager(true)
                    engine!!.load(url)
                }
            }
        }
    }

    private fun createButtonWithImage(icon: Icon): Button {
        val bImg = UIUtil.createImage(icon.iconWidth, icon.iconWidth, BufferedImage.TYPE_INT_ARGB)
        val graphics = bImg.createGraphics()
        icon.paintIcon(null, graphics, 0, 0)
        graphics.dispose()
        val image = SwingFXUtils.toFXImage(bImg, null)
        return Button(null, ImageView(image))
    }

    private fun makeProgressBarWithListener(): ProgressBar {
        val progress = ProgressBar()
        val loadWorker = engine!!.loadWorker
        progress.progressProperty().bind(loadWorker.progressProperty())

        loadWorker.stateProperty().addListener(
                object : ChangeListener<Worker.State> {
                    override fun changed(
                            ov: ObservableValue<out Worker.State>,
                            oldState: Worker.State,
                            newState: Worker.State) {
                        if (newState == Worker.State.CANCELLED) {
                            return
                        }

                        if (newState == Worker.State.FAILED) {
                            val map = HashMap<String, Any>()
                            map["url"] = engine!!.location
                            val content = Templater.processTemplate("error", map)
                            engine!!.loadContent(content)
                            return
                        }

                        val location = engine!!.location

                        if (location != null) {
                            if (location.startsWith("${Urls.STEPIK_URL}/#")) {
                                val paramString = location.split("#").dropLastWhile { it.isEmpty() }[1]
                                val params = paramString.split("&").dropLastWhile { it.isEmpty() }
                                map.clear()
                                params.forEach { param ->
                                    val entry = param.split("=").dropLastWhile { it.isEmpty() }
                                    var value = ""
                                    if (entry.size > 1) {
                                        value = entry[1]
                                    }
                                    map[entry[0]] = value
                                }
                                hide()
                                return
                            } else if ("${Urls.STEPIK_URL}/?error=access_denied" == location) {
                                map["error"] = "access_denied"
                                hide()
                                return
                            }
                        }

                        progressBar!!.isVisible = newState == Worker.State.RUNNING

                        if (newState == Worker.State.SUCCEEDED) {
                            this@AuthDialog.title = engine!!.title
                        }
                    }

                    private fun hide() {
                        loadWorker.cancel()
                        isVisible = false
                    }
                })

        return progress
    }

    companion object {

        fun showAuthForm(): Map<String, String> {
            val instance = AuthDialog()
            instance.isVisible = true
            val isCanceled = instance.map.isEmpty() || instance.map.containsKey("error")
            if (!instance.clearedCookies || !isCanceled) {
                instance.saveCookies()
            } else {
                // Restore cookies from store
                CookieManager.setDefault(CookieManager())
            }
            return instance.map
        }
    }
}
