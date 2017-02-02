package org.stepik.api.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author meanmail
 */
public interface TransportClient {
    @NotNull
    ClientResponse post(@NotNull StepikApiClient stepikApiClient, @NotNull String url, @Nullable String body);

    @NotNull
    ClientResponse get(@NotNull StepikApiClient stepikApiClient, @NotNull String url);

    @NotNull
    ClientResponse post(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull String url,
            @Nullable String body,
            @Nullable Map<String, String> headers);

    @NotNull
    ClientResponse get(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull String url,
            @Nullable Map<String, String> headers);
}
