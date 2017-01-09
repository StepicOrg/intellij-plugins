package org.stepik.api.objects.steps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author meanmail
 */
public class Sample extends ArrayList<String> {
    @NotNull
    public String getInput() {
        if (size() == 0) {
            setInput("");
        }
        return get(0);
    }

    public void setInput(@Nullable String input) {
        if (input == null) {
            input = "";
        }
        if (size() == 0) {
            add(input);
        } else {
            set(0, input);
        }
    }

    @NotNull
    public String getOutput() {
        if (size() < 2) {
            setOutput("");
        }
        return get(1);
    }

    public void setOutput(@Nullable String output) {
        if (output == null) {
            output = "";
        }
        if (size() == 0) {
            setInput("");
            add(output);
        } else if (size() == 1) {
            add(output);
        } else {
            set(1, output);
        }
    }
}
