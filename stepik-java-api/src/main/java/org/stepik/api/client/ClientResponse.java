package org.stepik.api.client;

import com.google.gson.Gson;

import java.util.Map;

/**
 * @author meanmail
 */
public class ClientResponse {
    private final Map<String, String> headers;
    private int statusCode;
    private String body;

    ClientResponse(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public <T> T getBody(Class<T> clazz) {
        return new Gson().fromJson(body, clazz);
    }
}
