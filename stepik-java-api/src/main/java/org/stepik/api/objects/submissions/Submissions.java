package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Submissions extends ObjectsContainer<Submission> {
    private List<Submission> submissions;

    @NotNull
    public List<Submission> getSubmissions() {
        if (submissions == null) {
            submissions = new ArrayList<>();
        }
        return submissions;
    }

    @NotNull
    @Override
    public List<Submission> getItems() {
        return getSubmissions();
    }

    @NotNull
    @Override
    public Class<Submission> getItemClass() {
        return Submission.class;
    }
}
