package org.stepik.api.objects.recommendations;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Reactions extends ObjectsContainer<Reaction> {
    @SerializedName("recommendation-reactions")
    private List<Reaction> reactions;

    @NotNull
    public List<Reaction> getReactions() {
        if (reactions == null) {
            reactions = new ArrayList<>();
        }
        return reactions;
    }

    @NotNull
    @Override
    public List<Reaction> getItems() {
        return getReactions();
    }

    @NotNull
    @Override
    public Class<Reaction> getItemClass() {
        return Reaction.class;
    }
}
