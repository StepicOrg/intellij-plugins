package org.stepik.api.objects.submissions;

/**
 * @author meanmail
 */
public class Reply {
    private String language;
    private String code;
    private String formula;
    private String[] attachments;
    private String text;
    private String[] files;
    private boolean[] choices;
    private Double[] ordering;
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
}
