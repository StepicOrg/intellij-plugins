package org.stepik.api.objects.views;

import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class ViewPost {
    private Long assignment;
    private long step;

    @Nullable
    public Long getAssignment() {
        return assignment;
    }

    public void setAssignment(@Nullable Long assignment) {
        this.assignment = assignment;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }
}
