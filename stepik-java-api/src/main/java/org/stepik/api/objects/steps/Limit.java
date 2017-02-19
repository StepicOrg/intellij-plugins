package org.stepik.api.objects.steps;

/**
 * @author meanmail
 */
public class Limit {
    private int time;
    private int memory;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Limit limit = (Limit) o;

        //noinspection SimplifiableIfStatement
        if (time != limit.time) return false;
        return memory == limit.memory;
    }

    @Override
    public int hashCode() {
        int result = time;
        result = 31 * result + memory;
        return result;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }
}
