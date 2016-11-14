package com.jetbrains.tmp.learning.stepik.metric;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

class MetricUtils {
    static boolean isAllNull(@NotNull Object... objects) {
        return Arrays.stream(objects).allMatch((x) -> x == null);
    }

    static boolean isAnyNull(@NotNull Object... objects) {
        return Arrays.stream(objects).anyMatch((x) -> x == null);
    }
}
