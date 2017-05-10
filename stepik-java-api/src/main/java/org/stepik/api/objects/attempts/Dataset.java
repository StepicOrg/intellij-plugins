package org.stepik.api.objects.attempts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Dataset {
    @SerializedName("is_multiple_choice")
    private boolean multipleChoice;
    private List<String> options;
    @SerializedName("is_text_disabled")
    private boolean textDisabled;
    private List<StringPair> pairs;
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

    public void setCheckbox(@Nullable Boolean checkbox) {
        checkbox = checkbox == null || checkbox;
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

    public void setMultipleChoice(@Nullable Boolean multipleChoice) {
        multipleChoice = multipleChoice == null || multipleChoice;
        this.multipleChoice = multipleChoice;
    }

    @NotNull
    public List<String> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
        }
        return options;
    }

    public void setOptions(@Nullable List<String> options) {
        this.options = options;
    }

    public boolean isTextDisabled() {
        return textDisabled;
    }

    public void setTextDisabled(@Nullable Boolean textDisabled) {
        textDisabled = textDisabled == null || textDisabled;
        this.textDisabled = textDisabled;
    }

    public List<StringPair> getPairs() {
        if (pairs == null) {
            pairs = new ArrayList<>();
        }
        return pairs;
    }

    public void setPairs(List<StringPair> pairs) {
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
        if (options != null ? !options.equals(dataset.options) : dataset.options != null) return false;
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
        result = 31 * result + (options != null ? options.hashCode() : 0);
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
