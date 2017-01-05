package org.stepik.api.objects.submissions;

import org.stepik.api.objects.ObjectsContainer;

import java.util.List;

/**
 * @author meanmail
 */
public class Submissions extends ObjectsContainer{
    private List<Submission> submissions;

    public boolean isEmpty() {
        return getCount() == 0;
    }

    private int getCount() {
        if (submissions == null) {
            return 0;
        }

        return submissions.size();
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }
}
