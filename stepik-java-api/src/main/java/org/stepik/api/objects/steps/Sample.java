package org.stepik.api.objects.steps;

/**
 * @author meanmail
 */
public class Sample {
    private String input;
    private String output;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Sample strings = (Sample) o;

        //noinspection SimplifiableIfStatement
        if (input != null ? !input.equals(strings.input) : strings.input != null) return false;
        return output != null ? output.equals(strings.output) : strings.output == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (input != null ? input.hashCode() : 0);
        result = 31 * result + (output != null ? output.hashCode() : 0);
        return result;
    }
}
