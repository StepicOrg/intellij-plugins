package org.stepik.api.objects.metrics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public class Metric {
    private String name;
    private Integer timestamp;
    private Map<String, String> tags;
    private Map<String, Object> data;

    public void addData(@NotNull String key, @NotNull Object value) {
        getData().put(key, value);
    }

    @NotNull
    public Map<String, Object> getData() {
        if (data == null) {
            data = new HashMap<>();
        }
        return data;
    }

    public void setData(@Nullable Map<String, Object> data) {
        this.data = data;
    }

    public void addTags(@NotNull String key, @NotNull String value) {
        getTags().put(key, value);
    }

    @NotNull
    public Map<String, String> getTags() {
        if (tags == null) {
            tags = new HashMap<>();
        }
        return tags;
    }

    public void setTags(@Nullable Map<String, String> tags) {
        this.tags = tags;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@Nullable Integer timestamp) {
        this.timestamp = timestamp;
    }
}
