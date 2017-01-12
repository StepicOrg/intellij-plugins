package org.stepik.api.objects;

import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class AbstractObjectWithStringId {
    private String id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractObjectWithStringId that = (AbstractObjectWithStringId) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }
}
