package org.stepik.api.objects.attempts;

import java.util.List;

/**
 * @author meanmail
 */
public class Component {
    private List<String> options;
    private String type;
    private String text;

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Component component = (Component) o;

        if (options != null ? !options.equals(component.options) : component.options != null) return false;
        //noinspection SimplifiableIfStatement
        if (type != null ? !type.equals(component.type) : component.type != null) return false;
        return text != null ? text.equals(component.text) : component.text == null;
    }

    @Override
    public int hashCode() {
        int result = options != null ? options.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
