package org.stepik.api.client;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class DefaultJsonConverter implements JsonConverter {
    private static JsonConverter instance;
    private final Gson gson = new Gson();

    @NotNull
    public static JsonConverter getInstance() {
        if (instance == null) {
            instance = new DefaultJsonConverter();
        }
        return instance;
    }

    @Nullable
    @Override
    public <T> T fromJson(@Nullable String json, @NotNull Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    @NotNull
    @Override
    public String toJson(@Nullable Object object) {
        return gson.toJson(object);
    }
}
