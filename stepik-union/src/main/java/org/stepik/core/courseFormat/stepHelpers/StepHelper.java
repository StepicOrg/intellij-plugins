package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;

/**
 * @author meanmail
 */
public class StepHelper {
    private final Project project;
    private final StepNode stepNode;

    public StepHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        this.project = project;
        this.stepNode = stepNode;
    }

    @NotNull
    StepNode getStepNode() {
        return stepNode;
    }

    @NotNull
    public String getType() {
        return stepNode.getType().getName();
    }

    @NotNull
    public String getLink() {
        StepNode stepNode = getStepNode();
        StudyNode parent = stepNode.getParent();
        if (parent != null) {
            return String.format("https://stepik.org/lesson/%d/step/%d", parent.getId(), stepNode.getPosition());
        }

        return "https://stepik.org/";
    }

    @NotNull
    public String getLinkTitle() {
        return String.format("This step can take place in the web version (%s)", getType());
    }

    @NotNull
    public String getContent() {
        String content = stepNode.getText();
        if (!content.startsWith("<p>") && !content.startsWith("<br>")) {
            content = "<p>" + content;
        }
        return content;
    }

    public boolean isAdaptive() {
        return StepikProjectManager.isAdaptive(project);
    }
}
