package com.jetbrains.tmp.learning.stepik.metric;

import java.util.Arrays;

public class MetricUtils {
    public static boolean isAllNull(Object... objects) {
        if (objects == null) return true;
        return Arrays.stream(objects).allMatch((x) -> x == null);
    }

    public static boolean isAnyNull(Object... objects) {
        if (objects == null) return true;
        return Arrays.stream(objects).anyMatch((x) -> x == null);
    }
}
