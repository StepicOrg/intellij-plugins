package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.courseFormat.StepNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author meanmail
 */
public class SortingQuizHelper extends QuizHelper {
    private List<Pair<Integer, String>> ordering;

    public SortingQuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    @SuppressWarnings("unused")
    @NotNull
    public List<Pair<Integer, String>> getOrdering() {
        initStepOptions();
        return ordering;
    }

    @Override
    protected void done() {
        List<Integer> replyOrdering = reply.getOrdering();
        List<String> values = getDataset().getOptions();

        int valuesCount = values.size();

        if (replyOrdering.size() != valuesCount) {
            replyOrdering = IntStream.range(0, valuesCount)
                    .boxed()
                    .collect(Collectors.toList());
        }
        ordering = replyOrdering.stream()
                .map(index -> Pair.create(index, index < valuesCount ? values.get(index) : ""))
                .collect(Collectors.toList());
    }

    @Override
    void fail() {
        ordering = new ArrayList<>();
    }

    @Override
    public boolean isAutoCreateAttempt() {
        return true;
    }
}
