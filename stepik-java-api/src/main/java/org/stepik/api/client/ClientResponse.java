package org.stepik.api.client;

import java.util.Map;

/**
 * @author meanmail
 */
public class ClientResponse {
    private final Map<String, String> headers;
    private final StepikApiClient stepikApiClient;
    private final int statusCode;
    private final String body;

    ClientResponse(StepikApiClient stepikApiClient, int statusCode, String body, Map<String, String> headers) {
        this.stepikApiClient = stepikApiClient;
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public <T> T getBody(Class<T> clazz) {
        return stepikApiClient.getJsonConverter().fromJson(body, clazz);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
