package org.stepik.core.ui;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.stepHelpers.VideoTheoryHelper;
import org.stepik.core.templates.Templater;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLInputElement;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StudyBrowserWindow extends JFrame {
    private static final Logger logger = Logger.getInstance(StudyBrowserWindow.class);
    private static final String EVENT_TYPE_CLICK = "click";
    private final Project project;
    private final JavaBridge bridge = new JavaBridge();
    private JFXPanel panel;
    private WebView webComponent;
    private StackPane pane;
    private WebEngine engine;

    StudyBrowserWindow(@NotNull Project project) {
        this.project = project;
        setSize(new Dimension(900, 800));
        setLayout(new BorderLayout());
        setPanel(new JFXPanel());
        setTitle("Study Browser");
        LafManager.getInstance().addLafManagerListener(new StudyLafManagerListener());
        initComponents();
    }

    private void updateLaf(boolean isDarcula) {
        if (isDarcula) {
            updateLafDarcula();
        } else {
            updateIntellijAndGTKLaf();
        }
    }

    private void updateIntellijAndGTKLaf() {
        Platform.runLater(() -> {
            final String scrollBarStyleUrl = getExternalURL("/style/javaFXBrowserScrollBar.css");
            pane.getStylesheets().add(scrollBarStyleUrl);
            engine.setUserStyleSheetLocation(null);
            engine.reload();
        });
    }

    private void updateLafDarcula() {
        Platform.runLater(() -> {
            final String engineStyleUrl = getExternalURL("/style/javaFXBrowserDarcula.css");
            final String scrollBarStyleUrl = getExternalURL("/style/javaFXBrowserDarculaScrollBar.css");
            engine.setUserStyleSheetLocation(engineStyleUrl);
            pane.getStylesheets().add(scrollBarStyleUrl);
            pane.setStyle("-fx-background-color: #3c3f41");
            panel.getScene().getStylesheets().add(engineStyleUrl);
            engine.reload();
        });
    }

    private void initComponents() {
        Platform.runLater(() -> {
            pane = new StackPane();
            webComponent = new WebView();
            engine = webComponent.getEngine();
            pane.getChildren().add(webComponent);
            initHyperlinkListener();
            initConsoleListener();
            Scene scene = new Scene(pane);
            panel.setScene(scene);
            panel.setVisible(true);
            updateLaf(LafManager.getInstance().getCurrentLookAndFeel() instanceof DarculaLookAndFeelInfo);
        });

        add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void initConsoleListener() {
        engine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != Worker.State.SUCCEEDED) {
                        return;
                    }
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("java", bridge);
                    @Language("JavaScript")
                    String script = "console.error = function (message) {\n" +
                            "    java.error(message);\n" +
                            "};\n" +
                            "console.warn = function (message) {\n" +
                            "    java.warn(message);\n" +
                            "};\n" +
                            "console.log = function (message) {\n" +
                            "    java.log(message);\n" +
                            "};\n" +
                            "console.debug = function (message) {\n" +
                            "    java.debug(message);\n" +
                            "};\n" +
                            "window.addEventListener('error', function (e) {\n" +
                            "    java.doError(e.filename, e.lineno, e.colno, e.message);\n" +
                            "    return true;\n" +
                            "});";
                    engine.executeScript(script);
                });
    }

    void loadContent(@NotNull String template, @NotNull Map<String, Object> params) {
        String content = getContent(template, params);
        Platform.runLater(() -> {
            Document document = engine.getDocument();
            if (document != null) {
                HTMLFormElement form = (HTMLFormElement) document.getElementById("answer_form");
                if (form != null) {
                    HTMLInputElement action = (HTMLInputElement) form.getElements().namedItem("action");
                    action.setValue("save_reply");
                    FormListener.handle(project, this, form);
                }
            }
            engine.loadContent(content);
        });
    }

    @NotNull
    private String getContent(@NotNull final String template, @NotNull Map<String, Object> params) {
        final EditorColorsScheme editorColorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
        int fontSize = editorColorsScheme.getEditorFontSize();

        Map<String, Object> map = new HashMap<>();
        map.put("font_size", String.valueOf(fontSize - 2));
        map.put("highlight", getExternalURL("/highlight/highlight.pack.js"));
        if (LafManager.getInstance().getCurrentLookAndFeel() instanceof DarculaLookAndFeelInfo) {
            map.put("css_highlight", getExternalURL("/highlight/styles/darcula.css"));
        } else {
            map.put("css_highlight", getExternalURL("/highlight/styles/idea.css"));
        }
        map.put("charset", Charset.defaultCharset().displayName());
        map.put("loader", getExternalURL("/templates/img/loader.svg"));
        map.put("login_css", getExternalURL("/templates/login/css/login.css"));
        map.putAll(params);

        return Templater.processTemplate(template, map);
    }

    private String getExternalURL(@NotNull String internalPath) {
        return getClass().getResource(internalPath).toExternalForm();
    }

    private void initHyperlinkListener() {
        engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                final EventListener linkListener = makeHyperLinkListener();
                addListenerToAllHyperlinkItems(linkListener);

                final EventListener formListener = new FormListener(project, this);
                final Document doc = engine.getDocument();
                ((EventTarget) doc).addEventListener(FormListener.EVENT_TYPE_SUBMIT, formListener, false);
            }
        });
    }

    private void addListenerToAllHyperlinkItems(EventListener listener) {
        final Document doc = engine.getDocument();
        if (doc != null) {
            final NodeList nodeList = doc.getElementsByTagName("a");
            for (int i = 0; i < nodeList.getLength(); i++) {
                ((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_CLICK, listener, false);
            }
        }
    }

    @NotNull
    private EventListener makeHyperLinkListener() {
        return new LinkListener(project, this, engine);
    }

    JFXPanel getPanel() {
        return panel;
    }

    private void setPanel(JFXPanel panel) {
        this.panel = panel;
    }

    void showLoadAnimation() {
        callFunction("showLoadAnimation");
    }

    void hideLoadAnimation() {
        callFunction("hideLoadAnimation");
    }

    void callFunction(@NotNull String name, @NotNull String... args) {
        Platform.runLater(() -> {
            try {
                String argsString = Arrays.stream(args)
                        .map(arg -> "\"" + arg + "\"")
                        .collect(Collectors.joining(","));
                String script = String.format("if (window.%1$s !== undefined) %1$s(%2$s);", name, argsString);
                engine.executeScript(script);
            } catch (JSException e) {
                logger.error(e);
            }
        });
    }

    private class StudyLafManagerListener implements LafManagerListener {
        @Override
        public void lookAndFeelChanged(LafManager manager) {
            updateLaf(manager.getCurrentLookAndFeel() instanceof DarculaLookAndFeelInfo);
        }
    }

    public class JavaBridge {
        public void log(String text) {
            logger.info("console: " + text);
        }

        public void error(String text) {
            logger.error("console: " + text);
        }

        public void warn(String text) {
            logger.warn("console: " + text);
        }

        public void debug(String text) {
            logger.debug("console: " + text);
        }

        public void doError(String filename, int lineno, int colno, String message) {
            error("\nfilename: " + filename + "\nline: " + lineno + "\ncolumn: " + colno + "\nmessage: " + message);
        }

        public void setVideoQuality(Integer value) {
            PropertiesComponent.getInstance()
                    .setValue(VideoTheoryHelper.VIDEO_QUALITY_PROPERTY_NAME, String.valueOf(value));
        }
    }
}
