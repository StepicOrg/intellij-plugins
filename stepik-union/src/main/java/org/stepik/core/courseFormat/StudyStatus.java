package org.stepik.core.courseFormat;

import org.jetbrains.annotations.Nullable;

public enum StudyStatus {
    UNCHECKED, SOLVED, FAILED, NEED_CHECK;

    public static StudyStatus of(@Nullable String status) {
        if (status == null) {
            return NEED_CHECK;
        }

        status = status.toLowerCase();

        if (status.equals("correct") || status.equals("solved")) {
            return SOLVED;
        }

        if (status.equals("wrong") || status.equals("failed")) {
            return FAILED;
        }

        return UNCHECKED;
    }
}
