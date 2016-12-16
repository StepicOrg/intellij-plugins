package com.jetbrains.tmp.learning.stepik.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author meanmail
 */
public class Meta {
    @Expose
    private int page;
    @Expose
    @SerializedName("has_next")
    private boolean hasNext;
    @Expose
    @SerializedName("has_previous")
    private boolean hasPrevious;

    public boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}