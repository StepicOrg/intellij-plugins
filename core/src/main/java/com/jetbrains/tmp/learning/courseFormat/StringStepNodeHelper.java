package com.jetbrains.tmp.learning.courseFormat;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class StringStepNodeHelper extends StepHelper {
    private boolean isTextDisabled;
    private String text;

    StringStepNodeHelper(@NotNull StepNode stepNode) {
        super(stepNode);
    }

    @NotNull
    public StepNode getStepNode() {
        return stepNode;
    }

    @NotNull
    public String getText() {
        initStepOptions();
        return text != null ? text : "";
    }

    @Override
    protected boolean needInit() {
        return text == null;
    }

    @Override
    protected void onStartInit() {
        text = "";
    }

    @Override
    protected void onAttemptLoaded() {
        isTextDisabled = dataset.isTextDisabled();
    }

    @Override
    protected void onSubmissionLoaded() {
        text = reply.getText();
        text = text != null ? text : "";
    }

    @Override
    protected void onInitFailed() {
        text = null;
    }

    public boolean isTextDisabled() {
        initStepOptions();
        return isTextDisabled;
    }
}
