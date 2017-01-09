package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Submissions extends ObjectsContainer {
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
    protected List getItems() {
        return getSubmissions();
    }
}
