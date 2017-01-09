package com.jetbrains.tmp.learning.stepik;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.ui.LoginPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LoginDialog extends DialogWrapper {
    private final LoginPanel loginPanel;

    LoginDialog() {
        super(false);
        loginPanel = new LoginPanel(this);
        setTitle("Login to Stepik");
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
        if (!validateLoginAndPasswordFields()) return;
        StepikUser basicUser = new StepikUser(loginPanel.getLogin(), loginPanel.getPassword());
        final StepikUser user = StepikConnectorLogin.minorLogin(basicUser);
        if (user != null) {
            doJustOkAction();
            final Project project = ProjectUtil.guessCurrentProject(loginPanel.getContentPanel());
            StepikProjectManager.getInstance(project).setUser(user);

            Project defaultProject = ProjectManager.getInstance().getDefaultProject();
            if (StepikProjectManager.getInstance(defaultProject).getUser().getEmail().isEmpty()) {
                StepikProjectManager.getInstance(defaultProject).setUser(user);
            }
        } else {
            setErrorText("Login failed");
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