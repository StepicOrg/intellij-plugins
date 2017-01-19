package org.stepik.api.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

/**
 * @author meanmail
 */
public interface TransportClient {
    @NotNull
    ClientResponse post(@NotNull StepikApiClient stepikApiClient, @NotNull String url, @Nullable String body)
            throws IOException;

    @NotNull
    ClientResponse get(@NotNull StepikApiClient stepikApiClient, @NotNull String url) throws IOException;

    @NotNull
    ClientResponse post(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull String url,
            @Nullable String body,
            @Nullable Map<String, String> headers)
            throws IOException;

    @NotNull
    ClientResponse get(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull String url,
            @Nullable Map<String, String> headers) throws IOException;
}
