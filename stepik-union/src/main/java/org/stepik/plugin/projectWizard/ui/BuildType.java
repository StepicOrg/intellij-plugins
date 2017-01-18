package org.stepik.plugin.projectWizard.ui;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public enum BuildType {
    COURSE_LINK, COURSE_LIST;

    @NotNull
    @Override
    public String toString() {
        switch (this) {
            case COURSE_LINK:
                return "Course link";
            case COURSE_LIST:
                return "Course list";
        }
        return super.toString();
    }
}
