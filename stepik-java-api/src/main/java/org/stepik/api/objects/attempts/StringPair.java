package org.stepik.api.objects.attempts;

/**
 * @author meanmail
 */
public class StringPair {
    private String first;
    private String second;

    public StringPair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        if (first == null) {
            first = "";
        }
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        if (second == null) {
            second = "";
        }
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringPair pair = (StringPair) o;

        //noinspection SimplifiableIfStatement
        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        return second != null ? second.equals(pair.second) : pair.second == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
