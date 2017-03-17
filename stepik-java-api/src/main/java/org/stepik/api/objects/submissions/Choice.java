package org.stepik.api.objects.submissions;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Choice {
    @SerializedName("name_row")
    private String nameRow;
    private List<Column> columns;

    public Choice(@Nullable String nameRow, @Nullable List<Column> columns) {
        this.nameRow = nameRow;
        this.columns = columns;
    }

    @NotNull
    public String getNameRow() {
        if (nameRow == null) {
            nameRow = "";
        }
        return nameRow;
    }

    public void setNameRow(@Nullable String nameRow) {
        this.nameRow = nameRow;
    }

    public List<Column> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        return columns;
    }

    public void setColumns(@Nullable List<Column> columns) {
        this.columns = columns;
    }
}
