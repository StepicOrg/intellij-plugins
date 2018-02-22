package org.stepik.core.auth.ui

import com.intellij.icons.AllIcons
import com.intellij.util.ui.UIUtil
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.stepik.api.urls.Urls
import org.stepik.core.auth.webkit.network.CookieManager
import org.stepik.core.stepik.StepikAuthManager
import org.stepik.core.templates.Templater
import org.stepik.core.ui.Browser
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Frame
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.Icon
import javax.swing.JDialog
import javax.swing.WindowConstants


class AuthBrowser internal constructor() : Browser() {
    internal val map = HashMap<String, String>()
    internal var clearedCookies: Boolean = false
    private val cookieManager = initCookieManager(false)
    private var url: String? = null
    private var progressBar: Node? = null

    override fun afterInitComponents() {
        val toolPane = HBox()
        toolPane.spacing = 5.0
        toolPane.alignment = Pos.CENTER_LEFT
        progressBar = makeProgressBarWithListener()
        pane!!.top = toolPane
        val backButton = makeGoBackButton()
        addButtonsAvailabilityListeners(backButton)
        val homeButton = makeHomeButton()
        val exitButton = makeExitButton()
        toolPane.children.addAll(backButton, homeButton, exitButton, progressBar)
        toolPane.padding = Insets(5.0)

        url = StepikAuthManager.implicitGrantUrl
        engine!!.load("https://stepik.org")
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

    internal fun saveCookies() {
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
            setOnAction {
                Platform.runLater {
                    engine!!.history.go(-1)
                }
            }
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
                    //                    initCookieManager(true)
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
                        val engine = engine!!
                        when (newState) {
                            Worker.State.CANCELLED -> return
                            Worker.State.FAILED -> {
                                val map = mapOf("url" to engine.location)
                                val content = Templater.processTemplate("error", map)
                                engine.loadContent(content)
                                return
                            }
                            else -> {
                                val location = engine.location

                                if (location != null) {
                                    if (location.startsWith("${Urls.STEPIK_URL}/#")) {
                                        val paramString = location.split("#", limit = 2)[1]
                                        val params = paramString.split("&")
                                        map.clear()
                                        params.forEach {
                                            val entry = it.split("=", limit = 2)
                                            map[entry.first()] = entry.getOrNull(1) ?: ""
                                        }
                                        hide()
                                        return
                                    } else if ("${Urls.STEPIK_URL}/?error=access_denied" == location) {
                                        map["error"] = "access_denied"
                                        hide()
                                        return
                                    }
                                }

                                progressBar?.isVisible = newState == Worker.State.RUNNING

                                if (newState == Worker.State.SUCCEEDED) {
                                    this@AuthBrowser.title = engine.title
                                }
                            }
                        }

                    }

                    private fun hide() {
                        loadWorker.cancel()
                        isVisible = false
                    }
                })

        return progress
    }
}


class AuthDialog : JDialog(null as Frame?, true) {
    internal val browser = AuthBrowser()

    init {
        title = "Authorize"
        size = Dimension(640, 480)
        setLocationRelativeTo(null)
        add(browser.panel, BorderLayout.CENTER)
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    }
}


fun showAuthForm(): Map<String, String> {
    val dialog = AuthDialog()
    dialog.isVisible = true
    val isCanceled = dialog.browser.map.isEmpty() || "error" in dialog.browser.map
    if (!dialog.browser.clearedCookies || !isCanceled) {
        dialog.browser.saveCookies()
    } else {
        // Restore cookies from store
        CookieManager.setDefault(CookieManager())
    }
    return dialog.browser.map
}
