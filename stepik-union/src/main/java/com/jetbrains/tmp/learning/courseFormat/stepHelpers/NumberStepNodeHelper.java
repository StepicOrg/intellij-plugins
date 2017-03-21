package com.jetbrains.tmp.learning.courseFormat.stepHelpers;

import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class NumberStepNodeHelper extends StepHelper {
    private String number;

    public NumberStepNodeHelper(@NotNull StepNode stepNode) {
        super(stepNode);
    }

    @NotNull
    public String getNumber() {
        initStepOptions();
        return StringEscapeUtils.escapeHtml(number != null ? number : "");
    }

    @Override
    protected boolean needInit() {
        return number == null;
    }

    @Override
    protected void onStartInit() {
        number = "";
    }

    @Override
    protected void onSubmissionLoaded() {
        number = reply.getNumber();
        number = number != null ? number : "";
    }

    @Override
    protected void onInitFailed() {
        number = null;
    }
}
