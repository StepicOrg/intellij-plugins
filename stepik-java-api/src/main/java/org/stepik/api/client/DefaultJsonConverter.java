package org.stepik.api.client;

import com.google.gson.Gson;

/**
 * @author meanmail
 */
public class DefaultJsonConverter implements JsonConverter{
    private final Gson gson = new Gson();
    private static JsonConverter instance;

    public static JsonConverter getInstance() {
        if (instance == null) {
            instance = new DefaultJsonConverter();
        }
        return instance;
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    @Override
    public String toJson(Object object) {
        return gson.toJson(object);
    }
}
