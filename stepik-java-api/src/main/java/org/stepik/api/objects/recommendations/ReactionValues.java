package org.stepik.api.objects.recommendations;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author meanmail
 */
public enum ReactionValues {
    UNKNOWN(Integer.MIN_VALUE), TOO_EASY(-1), TOO_HARD(0), INTERESTING(1), SOLVED(2);

    private static HashMap<String, ReactionValues> map;
    private final int value;

    ReactionValues(int value) {
        this.value = value;
    }

    public static ReactionValues of(@NotNull String value) {
        if (map == null) {
            map = new HashMap<>();
            Arrays.stream(values())
                    .forEach(reactionValues -> map.put(reactionValues.name().toLowerCase(), reactionValues));
        }
        return map.getOrDefault(value, UNKNOWN);
    }

    public int getValue() {
        return value;
    }

    public boolean in(ReactionValues... values) {
        for (ReactionValues value : values) {
            if (this.equals(value)) {
                return true;
            }
        }

        return false;
    }
}
