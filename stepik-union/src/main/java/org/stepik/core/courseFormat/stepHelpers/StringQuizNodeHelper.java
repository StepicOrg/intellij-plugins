package org.stepik.core.courseFormat.stepHelpers;

import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

/**
 * @author meanmail
 */
public class StringQuizNodeHelper extends QuizHelper {
    private boolean isTextDisabled;
    private String text;

    public StringQuizNodeHelper(@NotNull StepNode stepNode) {
        super(stepNode);
    }

    @NotNull
    public String getText() {
        initStepOptions();
        return StringEscapeUtils.escapeHtml(text != null ? text : "");
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
        isTextDisabled = getDataset().isTextDisabled();
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
