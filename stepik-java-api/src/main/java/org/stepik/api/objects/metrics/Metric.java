package org.stepik.api.objects.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public class Metric<T> {
    private String name;
    private Integer timestamp;
    private Map<String, String> tags;
    private Map<String, Object> data;

    public void addData(String key, Object value) {
        getData().put(key, value);
    }

    public Map<String, Object> getData() {
        if (data == null) {
            data = new HashMap<>();
        }
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void addTags(String key, String value) {
        getTags().put(key, value);
    }

    public Map<String, String> getTags() {
        if (tags == null) {
            tags = new HashMap<>();
        }
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }
}
