package org.stepik.api.objects.attempts;

import org.stepik.api.objects.ObjectsContainer;

import java.util.List;

/**
 * @author meanmail
 */
public class Attempts extends ObjectsContainer{
    private List<Attempt> attempts;

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    private int getCount() {
        if (attempts == null) {
            return 0;
        }

        return attempts.size();
    }
}
