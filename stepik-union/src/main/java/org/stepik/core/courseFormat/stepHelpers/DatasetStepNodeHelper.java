package org.stepik.core.courseFormat.stepHelpers;

import org.stepik.core.courseFormat.StepNode;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class DatasetStepNodeHelper extends StepHelper {
    private String data;

    public DatasetStepNodeHelper(@NotNull StepNode stepNode) {
        super(stepNode);
    }

    @NotNull
    public String getData() {
        initStepOptions();
        return StringEscapeUtils.escapeHtml(data != null ? data : "");
    }

    @Override
    protected boolean needInit() {
        return data == null;
    }

    @Override
    protected void onStartInit() {
        data = "";
    }

    @Override
    protected void onSubmissionLoaded() {
        data = reply.getText();
        data = data != null ? data : "";
    }

    @Override
    protected void onInitFailed() {
        data = null;
    }
}
