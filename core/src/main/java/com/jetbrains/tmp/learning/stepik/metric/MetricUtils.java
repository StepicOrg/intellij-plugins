package com.jetbrains.tmp.learning.stepik.metric;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MetricUtils {
    public static boolean isAllNull(@NotNull Object... objects) {
        return Arrays.stream(objects).allMatch((x) -> x == null);
    }

    public static boolean isAnyNull(@NotNull Object... objects) {
        return Arrays.stream(objects).anyMatch((x) -> x == null);
    }
}
