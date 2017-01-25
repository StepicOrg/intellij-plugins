package com.jetbrains.tmp.learning.stepik;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.tmp.learning.ui.LoginPanel;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.exceptions.StepikUnauthorizedException;
import org.stepik.core.utils.Utils;

import javax.swing.*;

public class LoginDialog extends DialogWrapper {
    private final LoginPanel loginPanel;

    LoginDialog() {
        super(false);
        loginPanel = new LoginPanel(this);
        setTitle("Authentication at Stepik");
        setOKButtonText("Login");
        init();
    }

    @NotNull
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }

    @Override
    protected JComponent createCenterPanel() {
        return loginPanel.getContentPanel();
    }

    @Override
    protected String getHelpId() {
        return "login_to_stepik";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return loginPanel.getPreferableFocusComponent();
    }

    @Override
    protected void doOKAction() {
        if (!validateLoginAndPasswordFields()) {
            return;
        }

        try {
            String login = loginPanel.getLogin();
            String password = loginPanel.getPassword();

            ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously((ThrowableComputable<Object, StepikClientException>) () -> {
                        ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                        StepikConnectorLogin.authenticate(login, password);
                        return null;
                    }, "Connection at Stepik", true, Utils.getCurrentProject());

            doJustOkAction();
        } catch (StepikUnauthorizedException e) {
            setErrorText("Wrong a login or a password");
        } catch (StepikClientException e) {
            setErrorText("Connection failed");
        }
    }

    private boolean validateLoginAndPasswordFields() {
        if (StringUtil.isEmptyOrSpaces(loginPanel.getLogin())) {
            setErrorText("Please, enter your username");
            return false;
        }
        if (StringUtil.isEmptyOrSpaces(loginPanel.getPassword())) {
            setErrorText("Please, enter your password");
            return false;
        }
        return true;
    }

    private void doJustOkAction() {
        super.doOKAction();
    }

    public void clearErrors() {
        setErrorText(null);
    }
}