package com.jetbrains.tmp.learning.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.jetbrains.tmp.learning.stepik.EduStepikNames;
import com.jetbrains.tmp.learning.stepik.LoginDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel {

    @SuppressWarnings("unused")
    private JPanel contentPanel;
    @SuppressWarnings("unused")
    private JPasswordField passwordField;
    @SuppressWarnings("unused")
    private JTextField loginField;
    @SuppressWarnings("unused")
    private JBLabel signUpLabel;

    public LoginPanel(final LoginDialog dialog) {
        DocumentListener listener = new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                dialog.clearErrors();
            }
        };

        loginField.getDocument().addDocumentListener(listener);
        passwordField.getDocument().addDocumentListener(listener);

        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.browse(EduStepikNames.STEPIK_SIGN_IN_LINK);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    public String getLogin() {
        return loginField.getText();
    }

    public JComponent getPreferableFocusComponent() {
        return loginField;
    }
}
