package org.stepik.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.util.Map;

/**
 * @author meanmail
 */
public interface TransportClient {
    @NotNull
    ClientResponse post(@NotNull String url, @Nullable String body) throws IOException;

    @NotNull
    ClientResponse get(@NotNull String url) throws IOException;

    @NotNull
    ClientResponse post(@NotNull String url, @Nullable String body, @Nullable Map<String, String> headers) throws IOException;

    @NotNull
    ClientResponse get(@NotNull String url, @Nullable Map<String, String> headers) throws IOException;
}
