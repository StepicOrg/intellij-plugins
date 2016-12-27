package org.stepik.plugin.collective.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.stepik.EduStepikNames;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import com.jetbrains.tmp.learning.stepik.StepikUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;

class StepikSettingsPanel {
    private static final String DEFAULT_PASSWORD_TEXT = "************";
    private final static String AUTH_PASSWORD = "Password";
    private final static String AUTH_TOKEN = "Token";
    private static final Logger logger = Logger.getInstance(StepikSettingsPanel.class);
    private Project settingsProject;
    private JTextField emailTextField;
    private JPasswordField passwordField;
    private JPasswordField tokenField; // look at createUIComponents() to understand
    private JTextPane signupTextField;
    private JPanel pane;
    private JButton testButton;
    private ComboBox<String> authTypeComboBox;
    private JPanel cardPanel;
    private JBLabel authTypeLabel;
    private JButton magicButton;
    private JCheckBox hintCheckBox;

    private boolean credentialsModified;
    private boolean hintCheckBoxModified;

    StepikSettingsPanel() {
        initProjectOfSettings();
        signupTextField.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            protected void hyperlinkActivated(final HyperlinkEvent e) {
                BrowserUtil.browse(e.getURL());
            }
        });
        magicButton.setText("Magic auth");
        signupTextField.setText("<html>Do not have an account at stepik.org? <a href=\"" +
                EduStepikNames.STEPIK_SIGN_IN_LINK + ">" + "Sign up" + "</a></html>");
        signupTextField.setBackground(pane.getBackground());
        signupTextField.setCursor(new Cursor(Cursor.HAND_CURSOR));
        authTypeLabel.setBorder(JBUI.Borders.emptyLeft(10));
        authTypeComboBox.addItem(AUTH_PASSWORD);
        hintCheckBox.setSelected(StepikProjectManager.getInstance(settingsProject).getShowHint());
        hintCheckBox.addActionListener(e -> hintCheckBoxModified = true);
        testButton.addActionListener(e -> {
            StepikProjectManager manager = StepikProjectManager.getInstance(settingsProject);
            StepikUser oldUser = manager.getUser();
            StepikUser testUser = new StepikUser(getEmail(), getPassword());
            manager.setUser(testUser);
            if (StepikConnectorLogin.loginFromSettings(settingsProject, testUser)) {
                String message = "Hello, " + manager.getUser().getName() + "!\n I am glad to see you.";
                Messages.showMessageDialog(message, "Check credentials", Messages.getInformationIcon());
            } else {
                Messages.showWarningDialog("Can't sign in.", "Check credentials");
            }
            manager.setUser(oldUser);
        });

        passwordField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                credentialsModified = true;
            }
        });

        DocumentListener passwordEraser = new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                if (!credentialsModified) {
                    erasePassword();
                }
            }
        };
        emailTextField.getDocument().addDocumentListener(passwordEraser);

        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!credentialsModified && !getPassword().isEmpty()) {
                    erasePassword();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        authTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = e.getItem().toString();
                if (AUTH_PASSWORD.equals(item)) {
                    ((CardLayout) cardPanel.getLayout()).show(cardPanel, AUTH_PASSWORD);
                } else if (AUTH_TOKEN.equals(item)) {
                    ((CardLayout) cardPanel.getLayout()).show(cardPanel, AUTH_TOKEN);
                }
                erasePassword();
            }
        });
    }

    private void erasePassword() {
        setPassword("");
        credentialsModified = true;
    }

    JComponent getPanel() {
        return pane;
    }

    @NotNull
    private String getEmail() {
        return emailTextField.getText().trim();
    }

    private void setLogin(@Nullable final String login) {
        emailTextField.setText(login);
    }

    @NotNull
    private String getPassword() {
        if (!credentialsModified) {
            initProjectOfSettings();
            logger.info("user's password");
            return StepikProjectManager.getInstance(settingsProject).getUser().getPassword();
        }
        return String.valueOf(passwordField.getPassword());
    }

    private void setPassword(@NotNull final String password) {
        // Show password as blank if password is empty
        passwordField.setText(StringUtil.isEmpty(password) ? null : password);
    }

    private void initProjectOfSettings() {
        if (settingsProject == null) {
            settingsProject = StudyUtils.getStudyProject();
        }
    }

    void reset() {
        initProjectOfSettings();
        final StepikUser user = StepikProjectManager.getInstance(settingsProject).getUser();
        setLogin(user.getEmail());
        setPassword(DEFAULT_PASSWORD_TEXT);
        resetCredentialsModification();
    }

    void apply() {
        if (credentialsModified) {
            initProjectOfSettings();
            StepikProjectManager manager = StepikProjectManager.getInstance(settingsProject);
            StepikUser basicUser = new StepikUser(getEmail(), getPassword());
            manager.setUser(basicUser);

            if (!StepikConnectorLogin.loginFromSettings(settingsProject, basicUser)) {
                Messages.showWarningDialog("Can't sign in.", "Check credentials");
            }
            logger.info(manager.getUser().toString());
        }
        if (hintCheckBoxModified) {
            StepikProjectManager manager = StepikProjectManager.getInstance(settingsProject);
            manager.setShowHint(hintCheckBox.isSelected());
        }
        resetCredentialsModification();
    }

    boolean isModified() {
        return credentialsModified || hintCheckBoxModified;
    }

    private void resetCredentialsModification() {
        credentialsModified = false;
        hintCheckBoxModified = false;
    }

    private void createUIComponents() {
        Document doc = new PlainDocument();
        passwordField = new JPasswordField(doc, null, 0);
    }
}
