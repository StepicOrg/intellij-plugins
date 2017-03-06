package org.stepik.api.objects.views;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Views extends ObjectsContainer<View> {
    private List<View> views;

    @NotNull
    public List<View> getViews() {
        if (views == null) {
            views = new ArrayList<>();
        }
        return views;
    }

    @NotNull
    @Override
    public List<View> getItems() {
        return getViews();
    }

    @NotNull
    @Override
    public Class<View> getItemClass() {
        return View.class;
    }
}
