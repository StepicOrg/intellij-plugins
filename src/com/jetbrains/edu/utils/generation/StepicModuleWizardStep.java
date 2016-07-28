package com.jetbrains.edu.utils.generation;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class StepicModuleWizardStep extends ModuleWizardStep {
    private JPanel panel;
    private JLabel courseLabel;
    private JComboBox courseSelecter;
    private final Project myProjectOrNull;

    public StepicModuleWizardStep(StepicCourseModuleBuilder builder, WizardContext context) {
        myProjectOrNull = context.getProject();

        initComponents();
    }

    private void initComponents() {
//        courseSelecter.addActionListener()

    }


    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void updateDataModel() {

    }

    @Override
    public void onStepLeaving() {
        saveSettings();
    }

    private void saveSettings() {
//        saveValue("login", textField1.getText());
//        saveValue("password", new String(passwordField1.getPassword()));
    }

    private static void saveValue(String key, String value) {
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


}
