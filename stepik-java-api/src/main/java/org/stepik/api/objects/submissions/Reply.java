package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author meanmail
 */
public class Reply {
    private String language;
    private String code;
    private String formula;
    private List<Attachment> attachments;
    private String text;
    private List<String> files;
    private List choices;
    private List<Integer> ordering;
    private String number;
    private String file;
    private List<String> blanks;

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

    @NotNull
    public String getFormula() {
        if (formula == null) {
            formula = "";
        }
        return formula;
    }

    public void setFormula(@Nullable String formula) {
        this.formula = formula;
    }

    @NotNull
    public List<Attachment> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return attachments;
    }

    public void setAttachments(@Nullable List<Attachment> attachments) {
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
        if (choices.size() > 1 && !(choices.get(0) instanceof Boolean)) {
            return Collections.emptyList();
        }
        //noinspection unchecked
        return choices;
    }

    public void setChoices(@Nullable List choices) {
        this.choices = choices;
    }

    @NotNull
    public List<Integer> getOrdering() {
        if (ordering == null) {
            ordering = new ArrayList<>();
        }
        return ordering;
    }

    public void setOrdering(@Nullable List<Integer> ordering) {
        this.ordering = ordering;
    }

    @Nullable
    public String getNumber() {
        return number;
    }

    public void setNumber(@Nullable String number) {
        this.number = number;
    }

    @Nullable
    public String getFile() {
        return file;
    }

    public void setFile(@Nullable String file) {
        this.file = file;
    }

    @NotNull
    public List<Choice> getTableChoices() {
        if (choices == null) {
            choices = new ArrayList<>();
        }
        if (choices.size() > 1 && !(choices.get(0) instanceof Choice)) {
            return Collections.emptyList();
        }
        //noinspection unchecked
        return choices;
    }

    @NotNull
    public List<String> getBlanks() {
        if (blanks == null) {
            blanks = new ArrayList<>();
        }
        return blanks;
    }

    public void setBlanks(@Nullable List<String> blanks) {
        this.blanks = blanks;
    }
}
