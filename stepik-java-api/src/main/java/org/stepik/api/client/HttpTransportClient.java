package org.stepik.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.AuthProvider;
import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public class HttpTransportClient implements TransportClient {
    private static final Logger logger = LogManager.getLogger(HttpTransportClient.class);

    private static final String ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String USER_AGENT = "Stepik Java API Client/" + StepikApiClient.getVersion();

    private static final int MAX_SIMULTANEOUS_CONNECTIONS = 300;
    private static final int FULL_CONNECTION_TIMEOUT_S = 60;
    private static final int CONNECTION_TIMEOUT_MS = 5_000;
    private static final int SOCKET_TIMEOUT_MS = FULL_CONNECTION_TIMEOUT_S * 1000;
    private static HttpTransportClient instance;

    private final CloseableHttpClient httpClient;

    public HttpTransportClient() {
        CookieStore cookieStore = new BasicCookieStore();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(MAX_SIMULTANEOUS_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_SIMULTANEOUS_CONNECTIONS);

        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .setUserAgent(USER_AGENT)
                .build();
    }

    @NotNull
    public static HttpTransportClient getInstance() {
        if (instance == null) {
            instance = new HttpTransportClient();
        }

        return instance;
    }

    @NotNull
    @Override
    public ClientResponse post(@NotNull String url, @Nullable String body) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE_HEADER, CONTENT_TYPE);

        return post(url, body, headers);
    }

    @NotNull
    @Override
    public ClientResponse get(@NotNull String url) throws IOException {
        return get(url, null);
    }

    @NotNull
    @Override
    public ClientResponse post(@NotNull String url, @Nullable String body, @Nullable Map<String, String> headers) throws IOException {
        HttpPost request = new HttpPost(url);
        if (headers != null) {
            headers.entrySet().forEach(entry -> request.setHeader(entry.getKey(), entry.getValue()));
        }
        if (body != null) {
            request.setEntity(new StringEntity(body));
        }
        return call(request);
    }

    @NotNull
    @Override
    public ClientResponse get(@NotNull String url, @Nullable Map<String, String> headers) throws IOException {
        HttpGet request = new HttpGet(url);
        if (headers != null) {
            headers.entrySet().forEach(entry -> request.setHeader(entry.getKey(), entry.getValue()));
        }
        return call(request);
    }

    @NotNull
    private ClientResponse call(HttpUriRequest request) throws IOException {
        HttpResponse response = httpClient.execute(request);

        try (InputStream content = response.getEntity().getContent()) {
            String result = IOUtils.toString(content, ENCODING);

            int statusCode = response.getStatusLine().getStatusCode();
            return new ClientResponse(statusCode, result, getHeaders(response.getAllHeaders()));
        }
    }

    @NotNull
    private Map<String, String> getHeaders(@Nullable Header[] headers) {
        Map<String, String> result = new HashMap<>();
        for (Header header : headers) {
            result.put(header.getName(), header.getValue());
        }

        return result;
    }
}
