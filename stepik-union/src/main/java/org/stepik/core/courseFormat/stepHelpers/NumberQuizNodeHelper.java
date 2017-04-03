package org.stepik.core.courseFormat.stepHelpers;

import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

/**
 * @author meanmail
 */
public class NumberQuizNodeHelper extends QuizHelper {
    private String number;

    public NumberQuizNodeHelper(@NotNull StepNode stepNode) {
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
