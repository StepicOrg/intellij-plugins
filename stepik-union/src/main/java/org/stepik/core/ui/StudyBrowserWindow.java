package org.stepik.core.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.recommendations.ReactionValues;
import org.stepik.api.objects.users.User;
import org.stepik.api.urls.Urls;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.StudyUtils;
import org.stepik.core.courseFormat.CourseNode;
import org.stepik.core.courseFormat.LessonNode;
import org.stepik.core.courseFormat.SectionNode;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.stepik.StepikAuthManager;
import org.stepik.core.templates.Templater;
import org.stepik.plugin.utils.NavigationUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.stepik.api.objects.recommendations.ReactionValues.SOLVED;
import static org.stepik.api.objects.recommendations.ReactionValues.TOO_EASY;
import static org.stepik.api.objects.recommendations.ReactionValues.TOO_HARD;
import static org.stepik.core.stepik.StepikAuthManager.getCurrentUser;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

class StudyBrowserWindow extends JFrame {
    private static final Logger logger = Logger.getInstance(StudyBrowserWindow.class);
    private static final String EVENT_TYPE_CLICK = "click";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Project project;
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
                    JSObject window = (JSObject) engine.executeScript("window");
                    JavaBridge bridge = new JavaBridge();
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

    void loadContent(@NotNull final String content) {
        String withCodeHighlighting = createHtmlWithCodeHighlighting(content);
        Platform.runLater(() -> engine.loadContent(withCodeHighlighting));
    }

    @NotNull
    private String createHtmlWithCodeHighlighting(@NotNull final String content) {
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
        map.put("code", content);
        map.put("charset", Charset.defaultCharset().displayName());

        return Templater.processTemplate("template", map);
    }

    private String getExternalURL(@NotNull String internalPath) {
        return getClass().getResource(internalPath).toExternalForm();
    }

    private void initHyperlinkListener() {
        engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                final EventListener linkListener = makeHyperLinkListener();
                addListenerToAllHyperlinkItems(linkListener);

                final EventListener formListener = new FormListener(project);
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
        return new EventListener() {
            private final Pattern pattern = Pattern.compile("/lesson(?:/|/[^/]*-)(\\d+)/step/(\\d+).*");

            @Override
            public void handleEvent(Event ev) {
                String domEventType = ev.getType();
                if (domEventType.equals(EVENT_TYPE_CLICK)) {
                    engine.setJavaScriptEnabled(true);
                    engine.getLoadWorker().cancel();
                    ev.preventDefault();
                    Element target = (Element) ev.getTarget();
                    String href = getLink(target);
                    if (href == null) {
                        return;
                    }

                    if (href.startsWith("inner:")) {
                        browseInnerLink(target, href);
                        return;
                    }

                    if (href.startsWith("adaptive:")) {
                        browseAdaptiveLink(href);
                        return;
                    }

                    if (browseProject(href)) {
                        return;
                    }

                    if (href.startsWith("/")) {
                        href = Urls.STEPIK_URL + href;
                    }

                    BrowserUtil.browse(href);
                }
            }

            private void browseInnerLink(@NotNull Element target, @NotNull String href) {
                href = href.substring(6);
                StudyNode root = StepikProjectManager.getProjectRoot(project);
                if (root == null) {
                    return;
                }

                String stepPath = target.getAttribute("data-step-path");

                StudyNode node = StudyUtils.getStudyNode(root, stepPath);
                if (node == null || !(node instanceof StepNode)) {
                    return;
                }

                String contentType = target.getAttribute("data-content-type");
                String prefix = target.getAttribute("data-file-prefix");
                String extension = target.getAttribute("data-file-ext");

                try {
                    StepikApiClient stepikApiClient = StepikAuthManager.getStepikApiClient();
                    String content = stepikApiClient.files().get(href, contentType);
                    VirtualFile srcDirectory = getOrCreateSrcDirectory(project, (StepNode) node, true);
                    if (srcDirectory == null) {
                        return;
                    }
                    Application application = ApplicationManager.getApplication();
                    application.invokeLater(() -> application.runWriteAction(() -> {
                        try {
                            int index = 1;
                            String filename = prefix + "_" + node.getId();
                            String currentFileName = filename + extension;
                            while (srcDirectory.findChild(currentFileName) != null) {
                                currentFileName = filename + "_" + index++ + extension;
                            }
                            VirtualFile file = srcDirectory.createChildData(null, currentFileName);
                            file.setBinaryContent(content.getBytes());
                            FileEditorManager.getInstance(project).openFile(file, false);
                        } catch (IOException e) {
                            logger.warn(e);
                        }
                    }));
                } catch (StepikClientException e) {
                    logger.warn(e);
                }
            }

            private void browseAdaptiveLink(@NotNull String href) {
                ReactionValues reaction;
                if (href.startsWith("adaptive:too_easy")) {
                    reaction = TOO_EASY;
                } else if (href.startsWith("adaptive:too_hard")) {
                    reaction = TOO_HARD;
                } else if (href.startsWith("adaptive:new_recommendation")) {
                    reaction = SOLVED;
                } else {
                    return;
                }

                showLoadAnimation();

                if (reaction == TOO_EASY || reaction == TOO_HARD) {
                    String[] items = href.split("/");
                    if (items.length < 2) {
                        return;
                    }

                    long lessonId;
                    try {
                        lessonId = Long.parseLong(items[1]);
                    } catch (NumberFormatException e) {
                        return;
                    }

                    try {

                        StepikApiClient stepikClient = StepikAuthManager.authAndGetStepikApiClient(true);
                        User user = getCurrentUser();
                        if (user.isGuest()) {
                            return;
                        }
                        stepikClient.recommendationReactions()
                                .post()
                                .user(user.getId())
                                .lesson(lessonId)
                                .reaction(reaction)
                                .execute();
                    } catch (StepikClientException e) {
                        logger.warn(e);
                    }
                }

                executor.execute(() -> {
                    StepikProjectManager.updateAdaptiveSelected(project);
                    hideLoadAnimation();
                });
            }

            private boolean browseProject(@NotNull String href) {
                Matcher matcher = pattern.matcher(href);
                if (matcher.matches()) {
                    long lessonId = Long.parseLong(matcher.group(1));
                    int stepPosition = Integer.parseInt(matcher.group(2));

                    StepNode step = null;

                    StudyNode root = StepikProjectManager.getProjectRoot(project);
                    if (root != null) {
                        if (root instanceof CourseNode) {
                            for (SectionNode section : ((CourseNode) root).getChildren()) {
                                LessonNode lessonNode = section.getChildById(lessonId);
                                if (lessonNode != null) {
                                    step = lessonNode.getChildByPosition(stepPosition);
                                }
                            }
                        } else if (root instanceof LessonNode) {
                            if (root.getId() == lessonId) {
                                step = ((LessonNode) root).getChildByPosition(stepPosition);
                            }
                        }

                        if (step != null) {
                            StepNode finalStep = step;
                            ApplicationManager.getApplication().invokeLater(() ->
                                    NavigationUtils.navigate(project, finalStep));
                            return true;
                        }
                    }
                }
                return false;
            }

            @Nullable
            private String getLink(@NotNull Element element) {
                final String href = element.getAttribute("href");
                return href == null ? getLinkFromNodeWithCodeTag(element) : href;
            }

            @Nullable
            private String getLinkFromNodeWithCodeTag(@NotNull Element element) {
                Node parentNode = element.getParentNode();
                NamedNodeMap attributes = parentNode.getAttributes();
                while (attributes.getLength() > 0 && attributes.getNamedItem("class") != null) {
                    parentNode = parentNode.getParentNode();
                    attributes = parentNode.getAttributes();
                }
                return attributes.getNamedItem("href").getNodeValue();
            }
        };
    }

    JFXPanel getPanel() {
        return panel;
    }

    private void setPanel(JFXPanel panel) {
        this.panel = panel;
    }

    void showLoadAnimation() {
        Platform.runLater(() -> {
            try {
                engine.executeScript("if (window.showLoadAnimation !== undefined) showLoadAnimation();");
            } catch (JSException e) {
                logger.error(e);
            }
        });
    }

    void hideLoadAnimation() {
        Platform.runLater(() -> {
            try {
                engine.executeScript("if (window.hideLoadAnimation !== undefined) hideLoadAnimation();");
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
    }
}
