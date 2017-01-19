package org.stepik.api.objects.attempts;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Attempts extends ObjectsContainer<Attempt> {
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
    public List<Attempt> getItems() {
        return getAttempts();
    }

    @NotNull
    @Override
    public Class<Attempt> getItemClass() {
        return Attempt.class;
    }
}
