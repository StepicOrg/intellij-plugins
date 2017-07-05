package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author meanmail
 */
public class ChoiceQuizHelper extends QuizHelper {
    private List<Pair<String, Boolean>> stepOptions;

    public ChoiceQuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    @Override
    protected void done() {
        List<Boolean> choices = reply.getChoices();
        List<String> options = getDataset().getOptions();
        if (choices.size() != options.size()) {
            choices = Collections.nCopies(options.size(), false);
        }
        List<Boolean> finalChoices = choices;
        stepOptions = IntStream.range(0, options.size())
                .boxed()
                .map(i -> Pair.create(options.get(i), finalChoices.get(i)))
                .collect(Collectors.toList());
    }

    @Override
    void fail() {
        stepOptions = new ArrayList<>();
    }

    @NotNull
    public List<Pair<String, Boolean>> getOptions() {
        initStepOptions();
        return stepOptions;
    }

    public boolean isMultipleChoice() {
        initStepOptions();
        return getDataset().isMultipleChoice();
    }

    @Override
    public boolean isAutoCreateAttempt() {
        return true;
    }
}
