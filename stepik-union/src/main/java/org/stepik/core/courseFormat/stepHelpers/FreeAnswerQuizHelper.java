package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

/**
 * @author meanmail
 */
public class FreeAnswerQuizHelper extends StringQuizHelper {
    public FreeAnswerQuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }
}
