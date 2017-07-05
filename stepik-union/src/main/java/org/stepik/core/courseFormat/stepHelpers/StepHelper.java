package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.objects.progresses.Progresses;
import org.stepik.api.urls.Urls;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.plugin.actions.navigation.StudyNavigator;

import static org.stepik.core.courseFormat.stepHelpers.Actions.NEED_LOGIN;
import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.isAuthenticated;

/**
 * @author meanmail
 */
public class StepHelper {
    private static final Logger logger = Logger.getInstance(StepHelper.class);
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
            return NEED_LOGIN.toString();
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
        String link = Urls.STEPIK_URL;
        if (parent != null) {
            link = String.format("%s/lesson/%d/step/%d", link, parent.getId(), stepNode.getPosition());
        }

        return link;
    }

    @NotNull
    public String getPath() {
        return getStepNode().getPath();
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

    public boolean solvedLesson() {
        try {
            StudyNode lesson = stepNode.getParent();
            if (lesson == null) {
                return false;
            }
            StudyObject data = lesson.getData();
            if (data == null) {
                return false;
            }
            String progressId = data.getProgress();
            StepikApiClient stepikApiClient = authAndGetStepikApiClient();
            Progresses progresses = stepikApiClient.progresses()
                    .get()
                    .id(progressId)
                    .execute();

            return !progresses.isEmpty() && progresses.getFirst().isPassed();
        } catch (StepikClientException e) {
            logger.warn(e);
        }

        return false;
    }

    public boolean hasNextStep() {
        return StudyNavigator.nextLeaf(stepNode) != null;
    }

    public boolean hasSubmitButton() {
        return false;
    }

    public boolean needLogin() {
        return "need_login".equals(getAction());
    }

    public boolean canSubmit() {
        return false;
    }

    public boolean isAutoCreateAttempt() {
        return hasSubmitButton();
    }
}
