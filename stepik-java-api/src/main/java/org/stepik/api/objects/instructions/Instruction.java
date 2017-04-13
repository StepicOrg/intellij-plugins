package org.stepik.api.objects.instructions;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Instruction extends AbstractObject {
    private long step;
    @SerializedName("min_reviews")
    private int minReviews;
    @SerializedName("strategy_type")
    private String strategyType;
    private List<Integer> rubrics;
    @SerializedName("is_frozen")
    private boolean isFrozen;
    private String text;

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public int getMinReviews() {
        return minReviews;
    }

    public void setMinReviews(int minReviews) {
        this.minReviews = minReviews;
    }

    @NotNull
    public String getStrategyType() {
        if (strategyType == null) {
            strategyType = "";
        }
        return strategyType;
    }

    public void setStrategyType(@Nullable String strategyType) {
        this.strategyType = strategyType;
    }

    @NotNull
    public List<Integer> getRubrics() {
        if (rubrics == null) {
            rubrics = new ArrayList<>();
        }
        return rubrics;
    }

    public void setRubrics(@Nullable List<Integer> rubrics) {
        this.rubrics = rubrics;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
    }

    @NotNull
    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }
}
