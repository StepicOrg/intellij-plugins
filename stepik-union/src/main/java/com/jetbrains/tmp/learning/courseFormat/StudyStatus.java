package com.jetbrains.tmp.learning.courseFormat;

import org.jetbrains.annotations.Nullable;

public enum StudyStatus {
    UNCHECKED, SOLVED, FAILED;

    public static StudyStatus of(@Nullable String status) {
        if (status == null) {
            return UNCHECKED;
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
