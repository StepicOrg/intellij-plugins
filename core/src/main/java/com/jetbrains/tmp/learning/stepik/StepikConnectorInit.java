package com.jetbrains.tmp.learning.stepik;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.net.HttpConfigurable;
import org.apache.http.HttpHost;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

class StepikConnectorInit {
    private static final Logger logger = Logger.getInstance(StepikConnectorInit.class.getName());
    @Nullable
    private static CloseableHttpClient ourClient;

    private static void initializeClient() {
        if (ourClient == null) {
            HttpClientBuilder builder = getBuilder();
            if (builder != null) {
                ourClient = builder.build();
            }
        }
    }

    @Nullable
    static HttpClientBuilder getBuilder() {
        HttpConfigurable instance = HttpConfigurable.getInstance();
        TrustManager[] trustAllCerts = getTrustAllCerts();
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpClientBuilder httpClientBuilder = HttpClients.custom()
                    .setMaxConnPerRoute(100000)
                    .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                    .setSSLContext(sslContext);

            if (instance.USE_HTTP_PROXY) {
                HttpHost host = new HttpHost(instance.PROXY_HOST, instance.PROXY_PORT);
                httpClientBuilder.setProxy(host);
                logger.info("Uses proxy: Host = " + instance.PROXY_HOST + " Port = " + instance.PROXY_PORT);
            }
            return httpClientBuilder;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    @Nullable
    static CloseableHttpClient getHttpClient() {
        if (ourClient == null) {
            initializeClient();
        }
        return ourClient;
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
}
