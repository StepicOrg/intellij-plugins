package com.jetbrains.edu.utils.generation;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class StepicModuleWizardStep extends ModuleWizardStep {
    private JPanel panel;
    private final Project myProjectOrNull;
    private StepikProjectGenerator generator;

    public StepicModuleWizardStep(StepikProjectGenerator generator, WizardContext context) {
        this.generator = generator;
        myProjectOrNull = context.getProject();

        initComponents();
    }

    private void initComponents() {
        panel = new StepicProjectPanel(generator);
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

    }

    private void createUIComponents() {

    }


}
