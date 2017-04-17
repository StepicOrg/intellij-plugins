package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;

import static org.stepik.core.stepik.StepikConnectorLogin.isAuthenticated;

/**
 * @author meanmail
 */
public class StepHelper {
    static final String NEED_LOGIN = "need_login";
    private final Project project;
    private final StepNode stepNode;

    public StepHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        this.project = project;
        this.stepNode = stepNode;
    }

    @NotNull
    public String getStatus() {
        return "";
    }

    @NotNull
    public String getAction() {
        if (!isAuthenticated()) {
            return NEED_LOGIN;
        }
        return "";
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
    public String getPath() {
        return getStepNode().getPath();
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

    public long getParent() {
        StudyNode parent = getStepNode().getParent();

        if (parent == null) {
            return 0;
        }

        return parent.getId();
    }
}
