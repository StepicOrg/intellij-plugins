package com.jetbrains.tmp.learning.stepik.entities;

import com.google.gson.annotations.Expose;

/**
 * @author meanmail
 */
public class SolutionFile {
    @Expose
    private String name;
    @Expose
    private String text;

    public SolutionFile(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}