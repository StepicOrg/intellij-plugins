package org.stepik.api.client;

import javafx.util.Pair;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
    private static final String USER_AGENT = "Stepik Java API Client/" + StepikApiClient.getVersion();

    private static final int MAX_SIMULTANEOUS_CONNECTIONS = 300;
    private static final int FULL_CONNECTION_TIMEOUT_S = 60;
    private static final int CONNECTION_TIMEOUT_MS = 5_000;
    private static final int SOCKET_TIMEOUT_MS = FULL_CONNECTION_TIMEOUT_S * 1000;
    private static final Map<Pair<String, Integer>, HttpTransportClient> instances = new HashMap<>();
    private static HttpTransportClient instance;
    private final CloseableHttpClient httpClient;

    private HttpTransportClient() {
        this(null, 0);
    }

    private HttpTransportClient(String proxyHost, int proxyPort) {
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

        HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .setUserAgent(USER_AGENT)
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE);

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustAllCerts(), new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.warn("Failed get instance SSL context", e);
        }
        if (sslContext != null) {
            builder.setSSLContext(sslContext);
        }

        if (proxyHost != null) {
            HttpHost host = new HttpHost(proxyHost, proxyPort);
            builder.setProxy(host);
        }

        httpClient = builder.build();
    }

    private static TrustManager[] getTrustAllCerts() {
        return new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
    }


    public static HttpTransportClient getInstance() {
        if (instance == null) {
            instance = new HttpTransportClient();
        }

        return instance;
    }

    public static HttpTransportClient getInstance(String proxyHost, int proxyPort) {
        Pair<String, Integer> proxy = new Pair<>(proxyHost, proxyPort);

        if (!instances.containsKey(proxy)) {
            HttpTransportClient instance = new HttpTransportClient(proxyHost, proxyPort);
            instances.put(proxy, instance);
            return instance;
        }

        return instances.get(proxy);
    }

    @Override
    public ClientResponse post(String url, String body) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE_HEADER, CONTENT_TYPE);

        return post(url, body, headers);
    }

    @Override
    public ClientResponse get(String url) throws IOException {
        return get(url, null);
    }

    @Override
    public ClientResponse post(String url, String body, Map<String, String> headers) throws IOException {
        HttpPost request = new HttpPost(url);
        if (headers != null) {
            headers.entrySet().forEach(entry -> request.setHeader(entry.getKey(), entry.getValue()));
        }
        if (body != null) {
            request.setEntity(new StringEntity(body));
        }
        return call(request);
    }

    @Override
    public ClientResponse get(String url, Map<String, String> headers) throws IOException {
        HttpGet request = new HttpGet(url);
        if (headers != null) {
            headers.entrySet().forEach(entry -> request.setHeader(entry.getKey(), entry.getValue()));
        }
        return call(request);
    }

    private ClientResponse call(HttpUriRequest request) throws IOException {
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        StringBuilder result = new StringBuilder();
        if (statusCode != StatusCodes.SC_NO_CONTENT) {
            try (BufferedReader content = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), ENCODING))) {

                String line;
                while ((line = content.readLine()) != null) {
                    result.append("\n").append(line);
                }

                if (result.length() > 0) {
                    result.deleteCharAt(0); // Delete first break line
                }
            }
        }

        return new ClientResponse(statusCode, result.toString(), getHeaders(response.getAllHeaders()));
    }

    private Map<String, String> getHeaders(Header[] headers) {
        Map<String, String> result = new HashMap<>();
        for (Header header : headers) {
            result.put(header.getName(), header.getValue());
        }

        return result;
    }
}
