package org.stepik.core.courseFormat.stepHelpers;

import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

/**
 * @author meanmail
 */
public class TextHelper extends StepHelper {
    public TextHelper(@NotNull StepNode stepNode) {
        super(stepNode);
    }

    @NotNull
    public String getLinkTitle() {
        return "Read this step on Stepik";
    }
}
