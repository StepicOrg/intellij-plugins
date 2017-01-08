package org.stepik.api.objects.submissions;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public List<String> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getFiles() {
        if (files == null) {
            files = new ArrayList<>();
        }
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<Boolean> getChoices() {
        if (choices == null) {
            choices = new ArrayList<>();
        }
        return choices;
    }

    public void setChoices(List<Boolean> choices) {
        this.choices = choices;
    }

    public List<Double> getOrdering() {
        if (ordering == null) {
            ordering = new ArrayList<>();
        }
        return ordering;
    }

    public void setOrdering(List<Double> ordering) {
        this.ordering = ordering;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
