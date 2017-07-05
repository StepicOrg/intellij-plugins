package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

/**
 * @author meanmail
 */
public class StringQuizHelper extends QuizHelper {
    public StringQuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    @NotNull
    public String getText() {
        initStepOptions();
        return reply.getText();
    }

    public boolean isTextDisabled() {
        initStepOptions();
        return getDataset().isTextDisabled();
    }

    @Override
    public boolean isAutoCreateAttempt() {
        return true;
    }
}
