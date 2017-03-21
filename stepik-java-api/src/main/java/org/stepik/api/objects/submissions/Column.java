package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class Column {
    private String name;
    private boolean answer;

    public Column(@Nullable String name, boolean answer) {
        this.name = name;
        this.answer = answer;
    }

    @NotNull
    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public boolean getAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
