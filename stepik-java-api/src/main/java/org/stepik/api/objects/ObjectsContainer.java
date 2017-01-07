package org.stepik.api.objects;

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

    protected abstract List getItems();

    public Meta getMeta() {
        if (meta == null) {
            meta = new Meta();
            meta.setPage(1);
        }
        return meta;
    }
}
