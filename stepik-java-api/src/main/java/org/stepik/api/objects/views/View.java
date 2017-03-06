package org.stepik.api.objects.views;

import org.stepik.api.objects.AbstractObject;

/**
 * @author meanmail
 */
public class View extends AbstractObject {
    private long assignment;
    private long step;

    public long getAssignment() {
        return assignment;
    }

    public void setAssignment(long assignment) {
        this.assignment = assignment;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }
}
