package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StepFile {
    @Nullable
    private String name;
    @Nullable
    private String text;
    @Nullable
    private Step step;

    public StepFile() {
    }

    public void initStepFile(@Nullable final Step step) {
        this.step = step;
    }

    @Nullable
    @Transient
    public Step getStep() {
        return step;
    }

    public void setStep(@Nullable Step step) {
        this.step = step;
    }

    @NotNull
    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @NotNull
    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }
}
