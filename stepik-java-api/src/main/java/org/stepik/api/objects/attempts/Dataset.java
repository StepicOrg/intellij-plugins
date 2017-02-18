package org.stepik.api.objects.attempts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author meanmail
 */
public class Dataset {
    @SerializedName("is_multiple_choice")
    private boolean isMultipleChoice;
    private String[] options;

    public boolean isMultipleChoice() {
        return isMultipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        isMultipleChoice = multipleChoice;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dataset dataset = (Dataset) o;

        if (isMultipleChoice != dataset.isMultipleChoice) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(options, dataset.options);
    }

    @Override
    public int hashCode() {
        int result = (isMultipleChoice ? 1 : 0);
        result = 31 * result + Arrays.hashCode(options);
        return result;
    }
}
