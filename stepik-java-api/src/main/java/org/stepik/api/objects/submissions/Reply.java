package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Reply {
    private String language;
    private String code;
    private String formula;
    private List<String> attachments;
    private String text;
    private List<String> files;
    private List<Boolean> choices;
    private List<Double> ordering;
    private String number;

    @NotNull
    public String getLanguage() {
        if (language == null) {
            language = "";
        }
        return language;
    }

    public void setLanguage(@Nullable String language) {
        this.language = language;
    }

    @NotNull
    public String getCode() {
        if (code == null) {
            code = "";
        }
        return code;
    }

    public void setCode(@Nullable String code) {
        this.code = code;
    }

    @Nullable
    public String getFormula() {
        return formula;
    }

    public void setFormula(@Nullable String formula) {
        this.formula = formula;
    }

    @NotNull
    public List<String> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return attachments;
    }

    public void setAttachments(@Nullable List<String> attachments) {
        this.attachments = attachments;
    }

    @Nullable
    public String getText() {
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    @NotNull
    public List<String> getFiles() {
        if (files == null) {
            files = new ArrayList<>();
        }
        return files;
    }

    public void setFiles(@Nullable List<String> files) {
        this.files = files;
    }

    @NotNull
    public List<Boolean> getChoices() {
        if (choices == null) {
            choices = new ArrayList<>();
        }
        return choices;
    }

    public void setChoices(@Nullable List<Boolean> choices) {
        this.choices = choices;
    }

    @NotNull
    public List<Double> getOrdering() {
        if (ordering == null) {
            ordering = new ArrayList<>();
        }
        return ordering;
    }

    public void setOrdering(@Nullable List<Double> ordering) {
        this.ordering = ordering;
    }

    @Nullable
    public String getNumber() {
        return number;
    }

    public void setNumber(@Nullable String number) {
        this.number = number;
    }
}
