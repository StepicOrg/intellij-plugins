package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

/**
 * @author meanmail
 */
public class TextTheoryHelper extends StepHelper {
    public TextTheoryHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    @NotNull
    public String getLinkTitle() {
        return "Read this step on Stepik";
    }
}
