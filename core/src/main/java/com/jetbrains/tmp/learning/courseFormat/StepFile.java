package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StepFile {
    private String name;
    private String text;
    private StepNode stepNode;

    public StepFile() {
    }

    public void init(@NotNull final StepNode stepNode) {
        this.stepNode = stepNode;
    }

    @Nullable
    @Transient
    public StepNode getStepNode() {
        return stepNode;
    }

    public void setStepNode(@Nullable StepNode stepNode) {
        this.stepNode = stepNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepFile stepFile = (StepFile) o;

        if (name != null ? !name.equals(stepFile.name) : stepFile.name != null) return false;
        //noinspection SimplifiableIfStatement
        if (text != null ? !text.equals(stepFile.text) : stepFile.text != null) return false;
        return stepNode != null ? stepNode.equals(stepFile.stepNode) : stepFile.stepNode == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (stepNode != null ? stepNode.hashCode() : 0);
        return result;
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

    @SuppressWarnings("unused")
    public void setText(@Nullable String text) {
        this.text = text;
    }
}
