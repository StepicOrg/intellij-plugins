package com.jetbrains.tmp.learning.courseFormat;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class StepHelper {
    protected final StepNode stepNode;

    StepHelper(@NotNull StepNode stepNode) {
        this.stepNode = stepNode;
    }

    @NotNull
    public StepNode getStepNode() {
        return stepNode;
    }
}
