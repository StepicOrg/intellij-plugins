package org.stepik.api.objects.submissions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author meanmail
 */
public class Choice {
    @SerializedName("name_row")
    private String nameRow;
    private List<Column> columns;

    public String getNameRow() {
        return nameRow;
    }

    public void setNameRow(String nameRow) {
        this.nameRow = nameRow;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
