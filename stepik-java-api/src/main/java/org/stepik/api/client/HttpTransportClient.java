package org.stepik.api.client;

import javafx.util.Pair;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stepik.api.exceptions.StepikClientException;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public class HttpTransportClient implements TransportClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpTransportClient.class);

    private static final String ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final int MAX_SIMULTANEOUS_CONNECTIONS = 100000;
    private static final int FULL_CONNECTION_TIMEOUT_S = 30;
    private static final int CONNECTION_TIMEOUT_MS = 5_000;
    private static final int SOCKET_TIMEOUT_MS = FULL_CONNECTION_TIMEOUT_S * 1000;
    private static final Map<Pair<String, Integer>, HttpTransportClient> instances = new HashMap<>();
    private static HttpTransportClient instance;
    private final CloseableHttpClient httpClient;

    private HttpTransportClient(@NotNull String userAgent) {
        this(null, 0, userAgent);
    }

    private HttpTransportClient(@Nullable String proxyHost, int proxyPort, @NotNull String userAgent) {
        CookieStore cookieStore = new BasicCookieStore();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();

        HttpClientBuilder builder = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .setMaxConnPerRoute(MAX_SIMULTANEOUS_CONNECTIONS)
                .setUserAgent(userAgent)
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE);

        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            builder.setSSLSocketFactory(sslSocketFactory);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            logger.warn("Failed set SSL connection socket factory", e);
        }

        if (proxyHost != null) {
            HttpHost host = new HttpHost(proxyHost, proxyPort);
            builder.setProxy(host);
        }

        httpClient = builder.build();
    }

    @NotNull
    public static HttpTransportClient getInstance(@NotNull String userAgent) {
        if (instance == null) {
            instance = new HttpTransportClient(userAgent);
        }

        return instance;
    }

    @NotNull
    public static HttpTransportClient getInstance(@Nullable String proxyHost, int proxyPort, @NotNull String userAgent) {
        Pair<String, Integer> proxy = new Pair<>(proxyHost, proxyPort);

        return instances.computeIfAbsent(proxy,
                k -> new HttpTransportClient(proxyHost, proxyPort, userAgent));
    }

    @NotNull
    @Override
    public ClientResponse post(@NotNull StepikApiClient stepikApiClient, @NotNull String url, @Nullable String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE_HEADER, CONTENT_TYPE);

        return post(stepikApiClient, url, body, headers);
    }

    @NotNull
    @Override
    public ClientResponse get(@NotNull StepikApiClient stepikApiClient, @NotNull String url) {
        return get(stepikApiClient, url, null);
    }

    @NotNull
    @Override
    public ClientResponse post(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull String url,
            @Nullable String body,
            @Nullable Map<String, String> headers) {
        HttpPost request = new HttpPost(url);
        if (headers == null) {
            headers = new HashMap<>();
        }

        headers.forEach(request::setHeader);

        if (body != null) {
            ContentType contentType;
            contentType = ContentType.create(headers.getOrDefault(CONTENT_TYPE_HEADER, CONTENT_TYPE), Consts.UTF_8);
            request.setEntity(new StringEntity(body, contentType));
        }
        return call(stepikApiClient, request);
    }

    @NotNull
    public ClientResponse get(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull String url,
            @Nullable Map<String, String> headers) {
        HttpGet request = new HttpGet(url);
        if (headers != null) {
            headers.forEach(request::setHeader);
        }
        return call(stepikApiClient, request);
    }

    @NotNull
    private ClientResponse call(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull HttpUriRequest request) {
        int statusCode;
        StringBuilder result;
        Map<String, String> headers;

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            result = new StringBuilder();

            if (entity != null) {
                try (BufferedReader content = new BufferedReader(
                        new InputStreamReader(entity.getContent(), ENCODING))) {

                    String line;
                    while ((line = content.readLine()) != null) {
                        result.append("\n").append(line);
                    }

                    if (result.length() > 0) {
                        result.deleteCharAt(0); // Delete first break line
                    }
                } catch (IOException | UnsupportedOperationException e) {
                    throw new StepikClientException("Failed getting a content", e);
                }
            }

            headers = getHeaders(response.getAllHeaders());
        } catch (IOException e) {
            throw new StepikClientException("Failed a request", e);
        }

        return new ClientResponse(stepikApiClient, statusCode, result.toString(), headers);
    }

    @NotNull
    private Map<String, String> getHeaders(@NotNull Header[] headers) {
        Map<String, String> result = new HashMap<>();
        for (Header header : headers) {
            result.put(header.getName(), header.getValue());
        }

        return result;
    }
}
