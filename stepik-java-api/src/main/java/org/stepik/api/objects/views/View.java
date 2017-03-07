package org.stepik.api.objects.views;

import org.stepik.api.objects.AbstractObject;

/**
 * @author meanmail
 */
public class View extends AbstractObject {
    private Long assignment;
    private long step;

    public Long getAssignment() {
        return assignment;
    }

    public void setAssignment(Long assignment) {
        this.assignment = assignment;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }
}
