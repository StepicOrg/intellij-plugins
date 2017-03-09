package org.stepik.api.objects.attempts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author meanmail
 */
public class Dataset {
    @SerializedName("is_multiple_choice")
    private boolean multipleChoice;
    private String[] options;
    @SerializedName("is_text_disabled")
    private boolean textDisabled;
    private List<Pair> pairs;

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    @NotNull
    public String[] getOptions() {
        if (options == null) {
            options = new String[0];
        }
        return options;
    }

    public void setOptions(@Nullable String[] options) {
        this.options = options;
    }

    public boolean isTextDisabled() {
        return textDisabled;
    }

    public void setTextDisabled(boolean textDisabled) {
        this.textDisabled = textDisabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dataset dataset = (Dataset) o;

        if (multipleChoice != dataset.multipleChoice) return false;
        if (textDisabled != dataset.textDisabled) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(options, dataset.options);
    }

    @Override
    public int hashCode() {
        int result = (multipleChoice ? 1 : 0);
        result = 31 * result + Arrays.hashCode(options);
        result = 31 * result + (textDisabled ? 1 : 0);
        return result;
    }

    public List<Pair> getPairs() {
        if (pairs == null) {
            pairs = new ArrayList<>();
        }
        return pairs;
    }

    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }
}
