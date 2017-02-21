package com.jetbrains.tmp.learning.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.StreamUtil;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyPluginConfigurator;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.SectionNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StudyBrowserWindow extends JFrame {
    private static final Logger logger = Logger.getInstance(StudyBrowserWindow.class);
    private static final String EVENT_TYPE_CLICK = "click";
    private final Project project;
    private JFXPanel panel;
    private WebView webComponent;
    private StackPane pane;

    private WebEngine engine;
    private ProgressBar progressBar;
    private boolean linkInNewBrowser = true;
    private boolean showProgress = false;

    StudyBrowserWindow(@NotNull Project project, final boolean linkInNewWindow, final boolean showProgress) {
        this.project = project;
        linkInNewBrowser = linkInNewWindow;
        this.showProgress = showProgress;
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
            final URL scrollBarStyleUrl = getClass().getResource("/style/javaFXBrowserScrollBar.css");
            pane.getStylesheets().add(scrollBarStyleUrl.toExternalForm());
            engine.setUserStyleSheetLocation(null);
            engine.reload();
        });
    }

    private void updateLafDarcula() {
        Platform.runLater(() -> {
            final URL engineStyleUrl = getClass().getResource("/style/javaFXBrowserDarcula.css");
            final URL scrollBarStyleUrl = getClass().getResource("/style/javaFXBrowserDarculaScrollBar.css");
            engine.setUserStyleSheetLocation(engineStyleUrl.toExternalForm());
            pane.getStylesheets().add(scrollBarStyleUrl.toExternalForm());
            pane.setStyle("-fx-background-color: #3c3f41");
            panel.getScene().getStylesheets().add(engineStyleUrl.toExternalForm());
            engine.reload();
        });
    }

    private void initComponents() {
        Platform.runLater(() -> {
            pane = new StackPane();
            webComponent = new WebView();
            engine = webComponent.getEngine();

            if (showProgress) {
                progressBar = makeProgressBarWithListener();
                webComponent.setVisible(false);
                pane.getChildren().addAll(webComponent, progressBar);
            } else {
                pane.getChildren().add(webComponent);
            }
            if (linkInNewBrowser) {
                initHyperlinkListener();
            }
            Scene scene = new Scene(pane);
            panel.setScene(scene);
            panel.setVisible(true);
            updateLaf(LafManager.getInstance().getCurrentLookAndFeel() instanceof DarculaLookAndFeelInfo);
        });

        add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    void loadContent(@NotNull final String content, @Nullable StudyPluginConfigurator configurator) {
        if (configurator == null) {
            Platform.runLater(() -> engine.loadContent(content));
        } else {
            String withCodeHighlighting = createHtmlWithCodeHighlighting(content, configurator);
            Platform.runLater(() -> {
                updateLookWithProgressBarIfNeeded();
                engine.loadContent(withCodeHighlighting);
            });
        }
    }

    @Nullable
    private String createHtmlWithCodeHighlighting(
            @NotNull final String content,
            @NotNull StudyPluginConfigurator configurator) {
        String template = null;
        InputStream stream = getClass().getResourceAsStream("/code-mirror/template.html");
        try {
            template = StreamUtil.readText(stream, "utf-8");
        } catch (IOException e) {
            logger.warn(e.getMessage());
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }

        if (template == null) {
            logger.warn("Code mirror template is null");
            return null;
        }

        final EditorColorsScheme editorColorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
        int fontSize = editorColorsScheme.getEditorFontSize();

        template = template.replace("${font_size}", String.valueOf(fontSize - 2));
        template = template.replace("${codemirror}",
                getClass().getResource("/code-mirror/codemirror.js").toExternalForm());
        template = template.replace("${language_script}", configurator.getLanguageScriptUrl());
        template = template.replace("${default_mode}", configurator.getDefaultHighlightingMode());
        template = template.replace("${runmode}", getClass().getResource("/code-mirror/runmode.js").toExternalForm());
        template = template.replace("${colorize}", getClass().getResource("/code-mirror/colorize.js").toExternalForm());
        template = template.replace("${javascript}",
                getClass().getResource("/code-mirror/javascript.js").toExternalForm());
        if (LafManager.getInstance().getCurrentLookAndFeel() instanceof DarculaLookAndFeelInfo) {
            template = template.replace("${css_oldcodemirror}",
                    getClass().getResource("/code-mirror/codemirror-old-darcula.css").toExternalForm());
            template = template.replace("${css_codemirror}",
                    getClass().getResource("/code-mirror/codemirror-darcula.css").toExternalForm());
        } else {
            template = template.replace("${css_oldcodemirror}",
                    getClass().getResource("/code-mirror/codemirror-old.css").toExternalForm());
            template = template.replace("${css_codemirror}",
                    getClass().getResource("/code-mirror/codemirror.css").toExternalForm());
        }
        template = template.replace("${code}", content);

        return template;
    }

    private void updateLookWithProgressBarIfNeeded() {
        if (showProgress) {
            progressBar.setVisible(true);
            webComponent.setVisible(false);
        }
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
                    final String href = getLink((Element) ev.getTarget());
                    if (href == null) {
                        return;
                    }

                    if (browseProject(href)) {
                        return;
                    }

                    BrowserUtil.browse(href);
                }
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

    void addBackAndOpenButtons() {
        ApplicationManager.getApplication().invokeLater(() -> {
            final JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            final JButton backButton = makeGoButton("Click to go back", AllIcons.Actions.Back, -1);
            final JButton forwardButton = makeGoButton("Click to go forward", AllIcons.Actions.Forward, 1);
            final JButton openInBrowser = new JButton(AllIcons.Actions.Browser_externalJavaDoc);
            openInBrowser.addActionListener(e -> BrowserUtil.browse(engine.getLocation()));
            openInBrowser.setToolTipText("Click to open link in browser");
            addButtonsAvailabilityListeners(backButton, forwardButton);

            panel.setMaximumSize(new Dimension(40, getPanel().getHeight()));
            panel.add(backButton);
            panel.add(forwardButton);
            panel.add(openInBrowser);

            add(panel, BorderLayout.PAGE_START);
        });
    }

    private void addButtonsAvailabilityListeners(JButton backButton, JButton forwardButton) {
        Platform.runLater(() -> engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                final WebHistory history = engine.getHistory();
                boolean isGoBackAvailable = history.getCurrentIndex() > 0;
                boolean isGoForwardAvailable = history.getCurrentIndex() < history.getEntries().size() - 1;
                ApplicationManager.getApplication().invokeLater(() -> {
                    backButton.setEnabled(isGoBackAvailable);
                    forwardButton.setEnabled(isGoForwardAvailable);
                });
            }
        }));
    }

    private JButton makeGoButton(@NotNull final String toolTipText, @NotNull final Icon icon, final int direction) {
        final JButton button = new JButton(icon);
        button.setEnabled(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Platform.runLater(() -> engine.getHistory().go(direction));
                }
            }
        });
        button.setToolTipText(toolTipText);
        return button;
    }


    private ProgressBar makeProgressBarWithListener() {
        final ProgressBar progress = new ProgressBar();
        progress.progressProperty().bind(webComponent.getEngine().getLoadWorker().progressProperty());

        webComponent.getEngine().getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (webComponent.getEngine()
                            .getLocation()
                            .contains("http") && newState == Worker.State.SUCCEEDED) {
                        progressBar.setVisible(false);
                        webComponent.setVisible(true);
                    }
                });

        return progress;
    }

    JFXPanel getPanel() {
        return panel;
    }

    private void setPanel(JFXPanel panel) {
        this.panel = panel;
    }

    private class StudyLafManagerListener implements LafManagerListener {
        @Override
        public void lookAndFeelChanged(LafManager manager) {
            updateLaf(manager.getCurrentLookAndFeel() instanceof DarculaLookAndFeelInfo);
        }
    }
}
