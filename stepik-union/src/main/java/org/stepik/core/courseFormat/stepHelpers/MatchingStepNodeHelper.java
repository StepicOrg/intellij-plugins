package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.util.Pair;
import org.stepik.core.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class MatchingStepNodeHelper extends StepHelper {
    private List<org.stepik.api.objects.attempts.Pair> values;
    private List<Integer> replyOrdering;
    private List<Pair<String, String>> ordering;

    public MatchingStepNodeHelper(@NotNull StepNode stepNode) {
        super(stepNode);
    }

    @NotNull
    public List<Pair<String, String>> getOrdering() {
        initStepOptions();
        return ordering;
    }

    @Override
    protected boolean needInit() {
        return ordering == null;
    }

    @Override
    protected void onStartInit() {
        ordering = new ArrayList<>();
    }

    @Override
    protected void onAttemptLoaded() {
        values = getDataset().getPairs();
    }

    @Override
    protected void onSubmissionLoaded() {
        replyOrdering = reply.getOrdering();
    }

    @Override
    protected void onFinishInit() {
        if (replyOrdering == null) {
            replyOrdering = new ArrayList<>();
            for (int i = 0; i < values.size(); i++)
                replyOrdering.add(i);
        }

        for (int i = 0; i < replyOrdering.size() && i < values.size(); i++) {
            int index = replyOrdering.get(i);
            String first = values.get(i).getFirst();
            String second = index < values.size() ? values.get(index).getSecond() : "";
            ordering.add(Pair.create(first, second));
        }
    }

    @Override
    void onInitFailed() {
        ordering = null;
    }
}
