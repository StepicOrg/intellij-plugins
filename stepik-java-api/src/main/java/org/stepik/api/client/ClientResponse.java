package org.stepik.api.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author meanmail
 */
public class ClientResponse {
    private final Map<String, String> headers;
    private final StepikApiClient stepikApiClient;
    private final int statusCode;
    private final String body;

    ClientResponse(
            @NotNull StepikApiClient stepikApiClient,
            int statusCode,
            @NotNull String body,
            @NotNull Map<String, String> headers) {
        this.stepikApiClient = stepikApiClient;
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    @NotNull
    public StepikApiClient getStepikApiClient() {
        return stepikApiClient;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Nullable
    public <T> T getBody(@NotNull Class<T> clazz) {
        return stepikApiClient.getJsonConverter().fromJson(body, clazz);
    }

    @NotNull
    public Map<String, String> getHeaders() {
        return headers;
    }
}
