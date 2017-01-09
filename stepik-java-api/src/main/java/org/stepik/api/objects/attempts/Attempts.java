package org.stepik.api.objects.attempts;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Attempts extends ObjectsContainer {
    private List<Attempt> attempts;

    @NotNull
    public List<Attempt> getAttempts() {
        if (attempts == null) {
            attempts = new ArrayList<>();
        }
        return attempts;
    }

    @NotNull
    @Override
    protected List getItems() {
        return getAttempts();
    }
}
