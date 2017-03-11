package org.stepik.plugin.auth.ui;

import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.stepik.api.urls.Urls;

import javax.swing.*;
import java.awt.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public class AuthDialog extends JDialog {
    private final Map<String, String> map = new HashMap<>();
    private WebEngine engine;
    private WebView webComponent;
    private Node progressBar;
    private JFXPanel panel;

    private AuthDialog(boolean clear) {
        super((Frame) null, true);
        setTitle("Authorize");
        setSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setPanel(new JFXPanel());
        Platform.runLater(() -> {
            StackPane pane = new StackPane();
            webComponent = new WebView();
            engine = webComponent.getEngine();
            progressBar = makeProgressBarWithListener();
            webComponent.setVisible(false);
            pane.getChildren().addAll(webComponent, progressBar);
            Scene scene = new Scene(pane);
            panel.setScene(scene);
            panel.setVisible(true);

            if (clear) {
                CookieManager manager = new CookieManager();
                CookieHandler.setDefault(manager);
                manager.getCookieStore().removeAll();
            }
            String url = StepikConnectorLogin.getImplicitGrantUrl();
            engine.load(url);
        });
        add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static Map<String, String> showAuthForm(boolean clear) {
        AuthDialog instance = new AuthDialog(clear);
        instance.setVisible(true);
        return instance.map;
    }

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
                        String location = engine.getLocation();
                        System.out.println(location);
                        if (location != null) {
                            if (location.startsWith(Urls.STEPIK_URL + "/#")) {
                                String paramString = location.split("#")[1];
                                String[] params = paramString.split("&");
                                map.clear();
                                Arrays.stream(params).forEach(param -> {
                                    String[] entry = param.split("=");
                                    String value = "";
                                    if (entry.length > 0) {
                                        value = entry[1];
                                    }
                                    map.put(entry[0], value);
                                });
                                hide();
                                return;
                            } else if ((Urls.STEPIK_URL + "/?error=access_denied").equals(location)) {
                                hide();
                                return;
                            }
                        }

                        if (newState == Worker.State.SUCCEEDED) {
                            progressBar.setVisible(false);
                            webComponent.setVisible(true);
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

    private void setPanel(JFXPanel panel) {
        this.panel = panel;
    }
}
