package org.stepik.api.objects;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author meanmail
 */
public abstract class ObjectsContainer {
    private Meta meta;

    public int getCount() {
        return getItems().size();
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    @NotNull
    protected abstract List getItems();

    @NotNull
    public Meta getMeta() {
        if (meta == null) {
            meta = new Meta();
            meta.setPage(1);
        }
        return meta;
    }
}
