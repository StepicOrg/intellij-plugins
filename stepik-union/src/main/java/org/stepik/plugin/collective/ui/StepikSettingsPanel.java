package org.stepik.plugin.collective.ui;

import org.stepik.core.StepikProjectManager;
import org.stepik.core.stepik.StepikConnectorLogin;
import org.stepik.core.utils.Utils;

import javax.swing.*;

class StepikSettingsPanel {
    private JPanel pane;
    private JCheckBox hintCheckBox;
    private JButton logoutButton;
    private JLabel userName;
    private JButton loginButton;

    private boolean hintCheckBoxModified;
    private StepikProjectManager projectManager;

    StepikSettingsPanel() {
        initProjectOfSettings();
        hintCheckBox.setSelected(projectManager != null && projectManager.getShowHint());
        hintCheckBox.addActionListener(e -> hintCheckBoxModified = true);

        logoutButton.addActionListener(e -> {
            StepikConnectorLogin.logout();
            updateUserName();
        });

        loginButton.addActionListener(e -> {
            StepikConnectorLogin.authentication();
            updateUserName();
        });
    }

    private void updateUserName() {
        userName.setText(StepikConnectorLogin.getCurrentUserFullName());
    }

    JComponent getPanel() {
        return pane;
    }

    private void initProjectOfSettings() {
        projectManager = StepikProjectManager.getInstance(Utils.getCurrentProject());
    }

    void reset() {
        initProjectOfSettings();
        resetModification();
        updateUserName();
    }

    void apply() {
        if (hintCheckBoxModified && projectManager != null) {
            projectManager.setShowHint(hintCheckBox.isSelected());
        }
        resetModification();
    }

    boolean isModified() {
        return hintCheckBoxModified;
    }

    private void resetModification() {
        hintCheckBoxModified = false;
    }
}
