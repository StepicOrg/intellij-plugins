package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.attempts.StringPair;
import org.stepik.core.courseFormat.StepNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author meanmail
 */
public class MatchingQuizHelper extends QuizHelper {
    private List<Pair<String, String>> ordering;

    public MatchingQuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    @Override
    protected void done() {
        List<StringPair> values = getDataset().getPairs();
        List<Integer> replyOrdering = reply.getOrdering();

        if (replyOrdering.size() != values.size()) {
            replyOrdering = IntStream.range(0, values.size())
                    .boxed()
                    .collect(Collectors.toList());
        }

        List<Integer> finalReplyOrdering = replyOrdering;
        ordering = IntStream.range(0, values.size())
                .boxed()
                .map(i -> {
                    int index = finalReplyOrdering.get(i);
                    String first = values.get(i).getFirst();
                    String second = index < values.size() ? values.get(index).getSecond() : "";
                    return Pair.create(first, second);
                })
                .collect(Collectors.toList());
    }

    @Override
    void fail() {
        ordering = new ArrayList<>();
    }

    @NotNull
    public List<Pair<String, String>> getOrdering() {
        initStepOptions();
        return ordering;
    }
}
