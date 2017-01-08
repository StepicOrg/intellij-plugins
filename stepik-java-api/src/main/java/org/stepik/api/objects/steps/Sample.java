package org.stepik.api.objects.steps;

import java.util.ArrayList;

/**
 * @author meanmail
 */
public class Sample extends ArrayList<String> {
    public String getInput() {
        if (size() == 0) {
            setInput("");
        }
        return get(0);
    }

    public void setInput(String input) {
        if (size() == 0) {
            add(input);
        } else {
            set(0, input);
        }
    }

    public String getOutput() {
        if (size() < 2) {
            setOutput("");
        }
        return get(1);
    }

    public void setOutput(String output) {
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
