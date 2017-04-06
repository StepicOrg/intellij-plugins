package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class ChoiceQuizNodeHelper extends QuizHelper {
    private boolean isMultipleChoice;
    private List<Pair<String, Boolean>> stepOptions;
    private String[] options;
    private List<Boolean> choices;

    public ChoiceQuizNodeHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    @NotNull
    public List<Pair<String, Boolean>> getOptions() {
        initStepOptions();
        return stepOptions;
    }

    @Override
    protected boolean needInit() {
        return stepOptions == null;
    }

    @Override
    protected void onStartInit() {
        stepOptions = new ArrayList<>();
    }

    @Override
    protected void onAttemptLoaded() {
        isMultipleChoice = getDataset().isMultipleChoice();
        options = getDataset().getOptions();
        choices = null;
    }

    @Override
    protected void onSubmissionLoaded() {
        choices = reply.getChoices();
    }

    @Override
    protected void onFinishInit() {
        for (int i = 0; i < options.length; i++) {
            boolean checked = choices != null && choices.get(i);
            stepOptions.add(Pair.create(options[i], checked));
        }
    }

    public boolean isMultipleChoice() {
        initStepOptions();
        return isMultipleChoice;
    }

    @Override
    void onInitFailed() {
        stepOptions = null;
    }
}
