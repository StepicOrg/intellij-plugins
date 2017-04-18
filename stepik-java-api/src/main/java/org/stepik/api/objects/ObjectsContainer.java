package org.stepik.api.objects;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author meanmail
 */
public abstract class ObjectsContainer<T> {
    private Meta meta;

    public int getCount() {
        return getItems().size();
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    @NotNull
    public abstract List<T> getItems();

    @NotNull
    public Meta getMeta() {
        if (meta == null) {
            meta = new Meta();
            meta.setPage(1);
        }
        return meta;
    }

    @NotNull
    public abstract Class<T> getItemClass();

    public T get(int index) {
        return getItems().get(index);
    }

    public T getFirst() {
        return getItems().get(0);
    }
}
