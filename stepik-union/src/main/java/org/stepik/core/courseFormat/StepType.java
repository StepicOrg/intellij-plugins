package org.stepik.core.courseFormat;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public enum StepType {
    UNKNOWN("unknown"),
    CODE("code"),
    TEXT("text"),
    VIDEO("video"),
    CHOICE("choice"),
    STRING("string"),
    SORTING("sorting"),
    MATCHING("matching"),
    NUMBER("number"),
    DATASET("dataset"),
    TABLE("table"),
    FILL_BLANKS("fill-blanks"),
    MATH("math"),
    FREE_ANSWER("free-answer");

    private static Map<String, StepType> map;
    private final String name;

    StepType(String name) {
        this.name = name;
    }

    @NotNull
    public static StepType of(String name) {
        if (map == null) {
            map = new HashMap<>();
            Arrays.stream(values())
                    .forEach(value -> map.put(value.name, value));
        }
        return map.getOrDefault(name, UNKNOWN);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}
