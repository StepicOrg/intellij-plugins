package org.stepik.api.objects.recommendations;

/**
 * @author meanmail
 */
public enum ReactionValues {
    TOO_EASY(-1), TOO_HARD(0), INTERESTING(1), SOLVED(2);

    private final int value;

    ReactionValues(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
