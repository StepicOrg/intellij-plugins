package org.stepik.plugin.auth.ui;

import com.intellij.icons.AllIcons;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.urls.Urls;
import org.stepik.core.stepik.StepikAuthManager;
import org.stepik.core.templates.Templater;
import org.stepik.plugin.auth.webkit.network.CookieManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.CookieHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public class AuthDialog extends JDialog {
    private final Map<String, String> map = new HashMap<>();
    private boolean clearedCookies;
    private CookieManager cookieManager;
    private String url;
    private WebEngine engine;
    private Node progressBar;
    private JFXPanel panel;

    private AuthDialog() {
        super((Frame) null, true);
        setTitle("Authorize");
        setSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setPanel(new JFXPanel());
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            BorderPane pane = new BorderPane();
            HBox toolPane = new HBox();
            toolPane.setSpacing(5);
            toolPane.setAlignment(Pos.CENTER_LEFT);
            WebView webComponent = new WebView();
            engine = webComponent.getEngine();
            progressBar = makeProgressBarWithListener();
            pane.setTop(toolPane);
            pane.setCenter(webComponent);
            Scene scene = new Scene(pane);
            panel.setScene(scene);
            panel.setVisible(true);

            Button backButton = makeGoBackButton();
            addButtonsAvailabilityListeners(backButton);
            Button homeButton = makeHomeButton();
            Button exitButton = makeExitButton();
            toolPane.getChildren().addAll(backButton, homeButton, exitButton, progressBar);
            toolPane.setPadding(new Insets(5));

            cookieManager = initCookieManager(false);

            url = StepikAuthManager.getImplicitGrantUrl();
            engine.load(url);
        });
        add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    @NotNull
    public static Map<String, String> showAuthForm() {
        AuthDialog instance = new AuthDialog();
        instance.setVisible(true);
        boolean isCanceled = instance.map.isEmpty() || instance.map.containsKey("error");
        if (!instance.clearedCookies || !isCanceled) {
            instance.saveCookies();
        } else {
            // Restore cookies from store
            CookieManager.setDefault(new CookieManager());
        }
        return instance.map;
    }

    @NotNull
    private CookieManager initCookieManager(boolean clearCookies) {
        CookieHandler cookieManager = CookieManager.getDefault();
        if (!(cookieManager instanceof CookieManager)) {
            cookieManager = new CookieManager();
        }

        if (clearCookies) {
            ((CookieManager) cookieManager).clear();
            clearedCookies = true;
        }

        CookieManager.setDefault(cookieManager);
        return (CookieManager) cookieManager;
    }

    private void saveCookies() {
        cookieManager.save();
    }

    private void addButtonsAvailabilityListeners(@NotNull Button goBackButton) {
        Platform.runLater(() -> engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                WebHistory history = engine.getHistory();
                boolean isGoBackDisable = history.getCurrentIndex() <= 0;
                goBackButton.setDisable(isGoBackDisable);
            }
        }));
    }

    @NotNull
    private Button makeGoBackButton() {
        Button button = createButtonWithImage(AllIcons.Actions.Back);
        button.setTooltip(new Tooltip("Back"));
        button.setDisable(true);
        button.setOnAction(event -> Platform.runLater(() -> engine.getHistory().go(-1)));

        return button;
    }

    @NotNull
    private Button makeHomeButton() {
        Button button = createButtonWithImage(AllIcons.Actions.Refresh);
        button.setTooltip(new Tooltip("To home"));
        button.setOnAction(event -> Platform.runLater(() -> engine.load(url)));

        return button;
    }

    @NotNull
    private Button makeExitButton() {
        Button button = createButtonWithImage(AllIcons.Actions.Exit);
        button.setTooltip(new Tooltip("Login to another account"));
        button.setOnAction(event -> Platform.runLater(() -> {
            initCookieManager(true);
            engine.load(url);
        }));

        return button;
    }

    @NotNull
    private Button createButtonWithImage(@NotNull Icon icon) {
        BufferedImage bImg = new BufferedImage(icon.getIconWidth(), icon.getIconWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bImg.createGraphics();
        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();
        WritableImage image = SwingFXUtils.toFXImage(bImg, null);
        return new Button(null, new ImageView(image));
    }

    @NotNull
    private ProgressBar makeProgressBarWithListener() {
        final ProgressBar progress = new ProgressBar();
        Worker<Void> loadWorker = engine.getLoadWorker();
        progress.progressProperty().bind(loadWorker.progressProperty());

        loadWorker.stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends Worker.State> ov,
                            Worker.State oldState,
                            Worker.State newState) {
                        if (newState == Worker.State.CANCELLED) {
                            return;
                        }

                        if (newState == Worker.State.FAILED) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("url", engine.getLocation());
                            String content = Templater.processTemplate("error", map);
                            engine.loadContent(content);
                            return;
                        }

                        String location = engine.getLocation();

                        if (location != null) {
                            if (location.startsWith(Urls.STEPIK_URL + "/#")) {
                                String paramString = location.split("#")[1];
                                String[] params = paramString.split("&");
                                map.clear();
                                Arrays.stream(params).forEach(param -> {
                                    String[] entry = param.split("=");
                                    String value = "";
                                    if (entry.length > 1) {
                                        value = entry[1];
                                    }
                                    map.put(entry[0], value);
                                });
                                hide();
                                return;
                            } else if ((Urls.STEPIK_URL + "/?error=access_denied").equals(location)) {
                                map.put("error", "access_denied");
                                hide();
                                return;
                            }
                        }

                        progressBar.setVisible(newState == Worker.State.RUNNING);

                        if (newState == Worker.State.SUCCEEDED) {
                            AuthDialog.this.setTitle(engine.getTitle());
                        }
                    }

                    private void hide() {
                        loadWorker.cancel();
                        setVisible(false);
                    }
                });

        return progress;
    }

    private void setPanel(@NotNull JFXPanel panel) {
        this.panel = panel;
    }
}
