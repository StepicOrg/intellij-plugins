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
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
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

public class StepikSettingsPanel {
    private static final String DEFAULT_PASSWORD_TEXT = "************";
    private final static String AUTH_PASSWORD = "Password";
    private final static String AUTH_TOKEN = "Token";
    private Project settingsProject = null;

    private static final Logger logger = Logger.getInstance(StepikSettingsPanel.class.getName());

    private JTextField myEmailTextField;
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
    private JCheckBox hintCheckBox;

    private boolean myCredentialsModified;
    private boolean myHintCheckBoxModified;

    public StepikSettingsPanel() {
        initProjectOfSettings();
        mySignupTextField.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            protected void hyperlinkActivated(final HyperlinkEvent e) {
                BrowserUtil.browse(e.getURL());
            }
        });
        magicButton.setText("Magic auth");
        mySignupTextField.setText(
                "<html>Do not have an account at stepik.org? <a href=\"https://stepik.org/registration\">" + "Sign up" + "</a></html>");
        mySignupTextField.setBackground(myPane.getBackground());
        mySignupTextField.setCursor(new Cursor(Cursor.HAND_CURSOR));
        myAuthTypeLabel.setBorder(JBUI.Borders.emptyLeft(10));
        myAuthTypeComboBox.addItem(AUTH_PASSWORD);
        hintCheckBox.setSelected(StudyTaskManager.getInstance(settingsProject).getShowHint());
        hintCheckBox.addActionListener(e -> myHintCheckBoxModified = true);
//        TODO later
//        myAuthTypeComboBox.addItem(AUTH_TOKEN);
//        final Project project = ProjectManager.getInstance().getDefaultProject();

        myTestButton.addActionListener(e -> {
            StudyTaskManager manager = StudyTaskManager.getInstance(settingsProject);
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
        myEmailTextField.getDocument().addDocumentListener(passwordEraser);

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
    }

    private void erasePassword() {
        setPassword("");
        myCredentialsModified = true;
    }

    public JComponent getPanel() {
        return myPane;
    }

    @NotNull
    public String getEmail() {
        return myEmailTextField.getText().trim();
    }

    public void setLogin(@Nullable final String login) {
        myEmailTextField.setText(login);
    }

    @NotNull
    private String getPassword() {
        if (!myCredentialsModified) {
            initProjectOfSettings();
            logger.info("user's password");
            return StudyTaskManager.getInstance(settingsProject).getUser().getPassword();
        }
        return String.valueOf(myPasswordField.getPassword());
    }

    private void initProjectOfSettings() {
        if (settingsProject == null) {
            settingsProject = StudyUtils.getStudyProject();
        }
    }

    private void setPassword(@NotNull final String password) {
        // Show password as blank if password is empty
        myPasswordField.setText(StringUtil.isEmpty(password) ? null : password);
    }

    public void reset() {
        initProjectOfSettings();
        final StepikUser user = StudyTaskManager.getInstance(settingsProject).getUser();
        setLogin(user.getEmail());
        setPassword(DEFAULT_PASSWORD_TEXT);
        resetCredentialsModification();
    }

    public void apply() {
        if (myCredentialsModified) {
            initProjectOfSettings();
            StudyTaskManager manager = StudyTaskManager.getInstance(settingsProject);
            StepikUser basicUser = new StepikUser(getEmail(), getPassword());
            manager.setUser(basicUser);

            if (!StepikConnectorLogin.loginFromSettings(settingsProject, basicUser)) {
                Messages.showWarningDialog("Can't sign in.", "Check credentials");
            }
            logger.info(manager.getUser().toString());
        }
        if (myHintCheckBoxModified) {
            StudyTaskManager manager = StudyTaskManager.getInstance(settingsProject);
            manager.setShowHint(hintCheckBox.isSelected());
        }
        resetCredentialsModification();
    }

    public boolean isModified() {
        return myCredentialsModified || myHintCheckBoxModified;
    }

    public void resetCredentialsModification() {
        myCredentialsModified = false;
    }

    private void createUIComponents() {
        Document doc = new PlainDocument();
        myPasswordField = new JPasswordField(doc, null, 0);
    }
}
