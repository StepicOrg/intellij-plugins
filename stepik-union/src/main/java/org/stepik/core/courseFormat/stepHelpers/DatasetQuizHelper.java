package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

/**
 * @author meanmail
 */
public class DatasetQuizHelper extends QuizHelper {
    private String data;

    public DatasetQuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
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
