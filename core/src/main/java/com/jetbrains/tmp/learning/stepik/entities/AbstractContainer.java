package com.jetbrains.tmp.learning.stepik.entities;

import com.google.gson.annotations.Expose;

/**
 * @author meanmail
 */
abstract class AbstractContainer {
    @Expose
    private Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}