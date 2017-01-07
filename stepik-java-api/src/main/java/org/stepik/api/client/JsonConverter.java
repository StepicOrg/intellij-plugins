package org.stepik.api.client;

/**
 * @author meanmail
 */
public interface JsonConverter {
    <T> T fromJson(String json, Class<T> clazz);
    String toJson(Object object);
}
