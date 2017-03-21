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
    private List<String> rows;
    private List<String> columns;
    @SerializedName("is_checkbox")
    private boolean isCheckbox;
    private String description;
    private List<Component> components;

    @NotNull
    public List<String> getRows() {
        if (rows == null) {
            rows = new ArrayList<>();
        }
        return rows;
    }

    public void setRows(@Nullable List<String> rows) {
        this.rows = rows;
    }

    @NotNull
    public List<String> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        return columns;
    }

    public void setColumns(@Nullable List<String> columns) {
        this.columns = columns;
    }

    public boolean isCheckbox() {
        return isCheckbox;
    }

    public void setCheckbox(boolean checkbox) {
        isCheckbox = checkbox;
    }

    @NotNull
    public String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

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

    public List<Pair> getPairs() {
        if (pairs == null) {
            pairs = new ArrayList<>();
        }
        return pairs;
    }

    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }

    @NotNull
    public List<Component> getComponents() {
        if (components == null) {
            components = new ArrayList<>();
        }
        return components;
    }

    public void setComponents(@Nullable List<Component> components) {
        this.components = components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dataset dataset = (Dataset) o;

        if (multipleChoice != dataset.multipleChoice) return false;
        if (textDisabled != dataset.textDisabled) return false;
        if (isCheckbox != dataset.isCheckbox) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(options, dataset.options)) return false;
        if (pairs != null ? !pairs.equals(dataset.pairs) : dataset.pairs != null) return false;
        if (rows != null ? !rows.equals(dataset.rows) : dataset.rows != null) return false;
        if (columns != null ? !columns.equals(dataset.columns) : dataset.columns != null) return false;
        //noinspection SimplifiableIfStatement
        if (description != null ? !description.equals(dataset.description) : dataset.description != null) return false;
        return components != null ? components.equals(dataset.components) : dataset.components == null;
    }

    @Override
    public int hashCode() {
        int result = (multipleChoice ? 1 : 0);
        result = 31 * result + Arrays.hashCode(options);
        result = 31 * result + (textDisabled ? 1 : 0);
        result = 31 * result + (pairs != null ? pairs.hashCode() : 0);
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        result = 31 * result + (isCheckbox ? 1 : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (components != null ? components.hashCode() : 0);
        return result;
    }
}
