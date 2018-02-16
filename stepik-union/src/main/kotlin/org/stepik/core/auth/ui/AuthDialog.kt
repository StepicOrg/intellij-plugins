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
    private var cookieManager: CookieManager? = null
    private var url: String? = null
    private var engine: WebEngine? = null
    private var progressBar: Node? = null
    private var panel: JFXPanel? = null

    init {
        title = "Authorize"
        size = Dimension(640, 480)
        setLocationRelativeTo(null)
        layout = BorderLayout()
        setPanel(JFXPanel())
        Platform.setImplicitExit(false)
        Platform.runLater {
            val pane = BorderPane()
            val toolPane = HBox()
            toolPane.spacing = 5.0
            toolPane.alignment = Pos.CENTER_LEFT
            val webComponent = WebView()
            engine = webComponent.engine
            progressBar = makeProgressBarWithListener()
            pane.top = toolPane
            pane.center = webComponent
            val scene = Scene(pane)
            panel!!.scene = scene
            panel!!.isVisible = true

            val backButton = makeGoBackButton()
            addButtonsAvailabilityListeners(backButton)
            val homeButton = makeHomeButton()
            val exitButton = makeExitButton()
            toolPane.children.addAll(backButton, homeButton, exitButton, progressBar)
            toolPane.padding = Insets(5.0)

            cookieManager = initCookieManager(false)

            url = StepikAuthManager.implicitGrantUrl
            engine!!.load(url)
        }
        add(panel!!, BorderLayout.CENTER)
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    }

    private fun initCookieManager(clearCookies: Boolean): CookieManager {
        var cookieManager = CookieManager.getDefault()
        if (cookieManager !is CookieManager) {
            cookieManager = CookieManager()
        }

        if (clearCookies) {
            cookieManager.clear()
            clearedCookies = true
        }

        CookieManager.setDefault(cookieManager)
        return cookieManager
    }

    private fun saveCookies() {
        cookieManager!!.save()
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
        val button = createButtonWithImage(AllIcons.Actions.Back)
        button.tooltip = Tooltip("Back")
        button.isDisable = true
        button.setOnAction { Platform.runLater { engine!!.history.go(-1) } }

        return button
    }

    private fun makeHomeButton(): Button {
        val button = createButtonWithImage(AllIcons.Actions.Refresh)
        button.tooltip = Tooltip("To home")
        button.setOnAction { Platform.runLater { engine!!.load(url) } }

        return button
    }

    private fun makeExitButton(): Button {
        val button = createButtonWithImage(AllIcons.Actions.Exit)
        button.tooltip = Tooltip("Login to another account")
        button.setOnAction {
            Platform.runLater {
                initCookieManager(true)
                engine!!.load(url)
            }
        }

        return button
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
                            if (location.startsWith(Urls.STEPIK_URL + "/#")) {
                                val paramString = location.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                                val params = paramString.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                map.clear()
                                Arrays.stream(params).forEach { param ->
                                    val entry = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    var value = ""
                                    if (entry.size > 1) {
                                        value = entry[1]
                                    }
                                    map[entry[0]] = value
                                }
                                hide()
                                return
                            } else if (Urls.STEPIK_URL + "/?error=access_denied" == location) {
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

    private fun setPanel(panel: JFXPanel) {
        this.panel = panel
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
