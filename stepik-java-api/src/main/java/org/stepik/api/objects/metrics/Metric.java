package org.stepik.api.objects.metrics;

import com.sun.istack.internal.NotNull;

import java.util.Map;

/**
 * @author meanmail
 */
public class Metric<T> {
    private String name;
    private Integer timestamp;
    private Map<String, String> tags;
    private Map<String, Object> data;

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public void setTags(@NotNull Map<String, String> tags) {
        this.tags = tags;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setName(String name) {
        this.name = name;
    }
}
