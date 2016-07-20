/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stepic.plugin.java.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.jetbrains.edu.learning.stepic.EduStepicConnector;
import com.jetbrains.edu.learning.stepic.StepicUser;
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

public class StepicSettingsPanel {
    private static final String DEFAULT_PASSWORD_TEXT = "************";
    private final static String AUTH_PASSWORD = "Password";
    private final static String AUTH_TOKEN = "Token";

    private static final Logger LOG = Logger.getInstance(StepicSettingsPanel.class.getName());

    private final StepicSettings mySettings;

    private JTextField myLoginTextField;
    private JPasswordField myPasswordField;
    private JPasswordField myTokenField; // look at createUIComponents() to understand
    private JTextPane mySignupTextField;
    private JPanel myPane;
    private JButton myTestButton;
    //  private JTextField myHostTextField;
    private ComboBox myAuthTypeComboBox;
    private JPanel myCardPanel;
    private JBLabel myAuthTypeLabel;
    private JButton magicButton;

    private boolean myCredentialsModified;

    public StepicSettingsPanel() {
        mySettings = StepicSettings.getInstance();
        mySignupTextField.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            protected void hyperlinkActivated(final HyperlinkEvent e) {
                BrowserUtil.browse(e.getURL());
            }
        });

        magicButton.setText("Magic auth");
        mySignupTextField.setText("<html>Do not have an account at stepic.org? <a href=\"https://stepic.org/registration\">" + "Sign up" + "</a></html>");
        mySignupTextField.setBackground(myPane.getBackground());
        mySignupTextField.setCursor(new Cursor(Cursor.HAND_CURSOR));
        myAuthTypeLabel.setBorder(JBUI.Borders.emptyLeft(10));
        myAuthTypeComboBox.addItem(AUTH_PASSWORD);
//        TODO later
//        myAuthTypeComboBox.addItem(AUTH_TOKEN);

        final Project project = ProjectManager.getInstance().getDefaultProject();

        myTestButton.addActionListener(e -> {

            final GithubAuthData auth = getAuthData();


            StepicUser user = EduStepicConnector.testLogin(this.getLogin(), auth.getBasicAuth().getPassword());
            if (user == null) {
                Messages.showWarningDialog("Can't sign in.", "Check credentials");
//                EduStepicConnector.resetClient();
            } else {
                String message = "Hello, " + user.getName() + "!\n I am glad to see you.";
                Messages.showMessageDialog(message, "Check credentials", Messages.getInformationIcon());
            }
//        final GithubAuthData auth = getAuthData();
//        GithubUser user = GithubUtil.computeValueInModalIO(project, "Access to GitHub", indicator ->
//          GithubUtil.checkAuthData(project, new GithubAuthDataHolder(auth), indicator));
//
//        if (GithubAuthData.AuthType.TOKEN.equals(getAuthType())) {
//          GithubNotifications.showInfoDialog(myPane, "Success", "Connection successful for user " + user.getLogin());
//        }
//        else {
//          GithubNotifications.showInfoDialog(myPane, "Success", "Connection successful");
//        }
//      }
//      catch (GithubAuthenticationException ex) {
//        GithubNotifications.showErrorDialog(myPane, "Login Failure", "Can't login using given credentials: ", ex);
//      }
//      catch (IOException ex) {
//        GithubNotifications.showErrorDialog(myPane, "Login Failure", "Can't login: ", ex);
//      }
        });
//
//    myCreateTokenButton.addActionListener(e -> {
//      try {
//        String newToken = GithubUtil.computeValueInModalIO(project, "Access to GitHub", indicator ->
//          GithubUtil.runTaskWithBasicAuthForHost(project, GithubAuthDataHolder.createFromSettings(), indicator, getHost(), connection ->
//            GithubApiUtil.getMasterToken(connection, "IntelliJ plugin")));
//        myPasswordField.setText(newToken);
//      }
//      catch (IOException ex) {
//        GithubNotifications.showErrorDialog(myPane, "Can't Create API Token", ex);
//      }
//    });

//        magicButton.addActionListener(e -> {
//            URI url = null;
//            try {
//                url = new URIBuilder("https://stepic.org/oauth2/authorize/")
//                        .addParameter("grant_type", "authorization-code")
//                        .addParameter("client_id", "NHpcRZlHp9PC6tsycZkYF6VL4dxsN8ik1rQlXtjK")
//                        .addParameter("redirect_uri", "http://localhost:36656")
//                        .build();
//            } catch (URISyntaxException ex) {
//                ex.printStackTrace();
//            }
//            LOG.info("Auth url created");
//
//            StepicUserAuthorizer authorizer = StepicUserAuthorizer.getInstance();
//
//            String token = authorizer.authorizeAndGetUser();
//            NewStepicConnector.initToken();
//            LOG.info("2token = " + token);
////            BrowserUtil.browse(url);
//            LOG.info("Url browsed");
//
//            LOG.info("2token = " + authorizer.getAccessToken());
//
//        });

        myPasswordField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                myCredentialsModified = true;
            }
        });

        DocumentListener passwordEraser = new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                if (!myCredentialsModified) {
                    erasePassword();
                }
            }
        };
        myLoginTextField.getDocument().addDocumentListener(passwordEraser);

        myPasswordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!myCredentialsModified && !getPassword().isEmpty()) {
                    erasePassword();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        myAuthTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = e.getItem().toString();
                if (AUTH_PASSWORD.equals(item)) {
                    ((CardLayout) myCardPanel.getLayout()).show(myCardPanel, AUTH_PASSWORD);
                } else if (AUTH_TOKEN.equals(item)) {
                    ((CardLayout) myCardPanel.getLayout()).show(myCardPanel, AUTH_TOKEN);
                }
                erasePassword();
            }
        });

        reset();
    }

    private void erasePassword() {
        setPassword("");
        myCredentialsModified = true;
    }

    public JComponent getPanel() {
        return myPane;
    }

    @NotNull
    public String getLogin() {
        return myLoginTextField.getText().trim();
    }

    public void setLogin(@Nullable final String login) {
        myLoginTextField.setText(login);
    }

    @NotNull
    private String getPassword() {
        return String.valueOf(myPasswordField.getPassword());
    }

    private void setPassword(@NotNull final String password) {
        // Show password as blank if password is empty
        myPasswordField.setText(StringUtil.isEmpty(password) ? null : password);
    }

    @NotNull
    public GithubAuthData.AuthType getAuthType() {
        Object selected = myAuthTypeComboBox.getSelectedItem();
        if (AUTH_PASSWORD.equals(selected)) return GithubAuthData.AuthType.BASIC;
        if (AUTH_TOKEN.equals(selected)) return GithubAuthData.AuthType.TOKEN;
        LOG.error("StepicSettingsPanel: illegal selection: basic AuthType returned", selected.toString());
        return GithubAuthData.AuthType.BASIC;
    }

    public void setAuthType(@NotNull final GithubAuthData.AuthType type) {
        switch (type) {
            case BASIC:
                myAuthTypeComboBox.setSelectedItem(AUTH_PASSWORD);
                break;
            case TOKEN:
                myAuthTypeComboBox.setSelectedItem(AUTH_TOKEN);
                break;
            case ANONYMOUS:
            default:
                myAuthTypeComboBox.setSelectedItem(AUTH_PASSWORD);
        }
    }

    @NotNull
    public GithubAuthData getAuthData() {
        if (!myCredentialsModified) {
            return mySettings.getAuthData();
        }
        Object selected = myAuthTypeComboBox.getSelectedItem();
        if (AUTH_PASSWORD.equals(selected)) return GithubAuthData.createBasicAuth(getLogin(), getPassword());
//    if (AUTH_TOKEN.equals(selected)) return GithubAuthData.createTokenAuth(getHost(), StringUtil.trim(getPassword()));
//    LOG.error("StepicSettingsPanel: illegal selection: anonymous AuthData created", selected.toString());
//    return GithubAuthData.createAnonymous(getHost());
        return GithubAuthData.createAnonymous();
    }

    public void reset() {
        setLogin(mySettings.getLogin());
        setPassword(mySettings.isAuthConfigured() ? DEFAULT_PASSWORD_TEXT : "");
        setAuthType(mySettings.getAuthType());
        resetCredentialsModification();
    }

    public void apply() {
        if (myCredentialsModified) {
            mySettings.setAuthData(getAuthData(), true);
        }
        resetCredentialsModification();
    }

    public boolean isModified() {
        return myCredentialsModified;
    }

    public void resetCredentialsModification() {
        myCredentialsModified = false;
    }

    private void createUIComponents() {
        Document doc = new PlainDocument();
        myPasswordField = new JPasswordField(doc, null, 0);
        myTokenField = new JPasswordField(doc, null, 0);
    }
}
