package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.instructions.Instruction;
import org.stepik.api.objects.instructions.Instructions;
import org.stepik.api.objects.steps.Step;
import org.stepik.core.courseFormat.StepNode;

import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.isAuthenticated;

/**
 * @author meanmail
 */
public class FreeAnswerQuizHelper extends StringQuizHelper {
    public FreeAnswerQuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
        useLastSubmission = true;
    }

    public boolean withReview() {
        initStepOptions();
        Step data = getStepNode().getData();
        return data != null && data.getInstruction() != 0;
    }

    private boolean needSolve() {
        return needSendSubmission() && !"correct".equals(getStatus());
    }

    private boolean needSendSubmission() {
        initStepOptions();
        Step data = getStepNode().getData();
        return data == null || data.getSession() == 0;
    }

    private boolean needReview() {
        if (needSendSubmission()) {
            return false;
        }
        Step data = getStepNode().getData();
        return data == null || data.getActions().containsKey("do_review");
    }

    private boolean isFrozen() {
        if (!needReview()) {
            return true;
        }

        Step data = getStepNode().getData();
        if (data == null) {
            return true;
        }

        int instructionId = data.getInstruction();
        if (instructionId == 0) {
            return true;
        }

        try {
            StepikApiClient stepikClient = authAndGetStepikApiClient();
            if (!isAuthenticated()) {
                return true;
            }
            Instructions instructions = stepikClient.instructions()
                    .get()
                    .id(instructionId)
                    .execute();
            if (!instructions.isEmpty()) {
                Instruction instruction = instructions.getFirst();
                return instruction.isFrozen();
            }
        } catch (StepikClientException ignored) {
        }
        return true;
    }

    @SuppressWarnings("SameReturnValue")
    private boolean needWait() {
        // TODO not implemented
        return false;
    }

    @SuppressWarnings("SameReturnValue")
    private boolean complete() {
        // TODO not implemented
        return false;
    }

    public String getStageText() {
        if (complete()) {
            return "Complete";
        }

        if (needWait()) {
            return "Stage 4 of 4: Wait when other students review for your  submission";
        }

        if (needReview()) {
            return "Stage 3 of 4: You should review submissions of other students";
        }

        if (needSendSubmission()) {
            return "Stage 2 of 4: You should send the solution for review";
        }

        if (needSolve()) {
            return "Stage 1 of 4: You should solve the problem";
        }

        return "";
    }

    public boolean hasAction() {
        return needReview() || needSendSubmission();
    }

    public String getActionName() {
        if (needReview()) {
            return "Start to review";
        }

        if (needSendSubmission()) {
            return "Send the solution for review";
        }
        return "";
    }

    @SuppressWarnings("unused")
    public boolean isActionEnabled() {
        return !needReview() || !isFrozen();
    }

    public String getActionHint() {
        if (needReview() && isFrozen()) {
            return "Not solutions for review yet";
        }
        return "";
    }
}
