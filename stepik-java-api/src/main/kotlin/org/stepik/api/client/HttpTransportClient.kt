package org.stepik.api.client

import org.apache.http.Consts
import org.apache.http.HttpHost
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.DefaultConnectionReuseStrategy
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.slf4j.LoggerFactory
import org.stepik.api.exceptions.StepikClientException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.*

class HttpTransportClient private constructor(proxyHost: String?, proxyPort: Int, userAgent: String) : TransportClient {
    
    private val httpClient: CloseableHttpClient
    
    private constructor(userAgent: String) : this(null, 0, userAgent)
    
    init {
        val cookieStore = BasicCookieStore()
        val requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build()
        
        val builder = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .setMaxConnPerRoute(MAX_SIMULTANEOUS_CONNECTIONS)
                .setUserAgent(userAgent)
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
        
        try {
            val sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null) { _, _ -> true }
                    .build()
            
            val sslSocketFactory = SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE)
            
            builder.setSSLSocketFactory(sslSocketFactory)
        } catch (e: NoSuchAlgorithmException) {
            logger.warn("Failed set SSL connection socket factory", e)
        } catch (e: KeyManagementException) {
            logger.warn("Failed set SSL connection socket factory", e)
        } catch (e: KeyStoreException) {
            logger.warn("Failed set SSL connection socket factory", e)
        }
        
        if (proxyHost != null) {
            val host = HttpHost(proxyHost, proxyPort)
            builder.setProxy(host)
        }
        
        httpClient = builder.build()
    }
    
    override fun post(stepikApiClient: StepikApiClient, url: String, body: String?): ClientResponse {
        val headers = HashMap<String, String>()
        headers[CONTENT_TYPE_HEADER] = CONTENT_TYPE
        
        return post(stepikApiClient, url, body, headers)
    }
    
    override fun get(stepikApiClient: StepikApiClient, url: String): ClientResponse {
        return get(stepikApiClient, url, null)
    }
    
    override fun post(
            stepikApiClient: StepikApiClient,
            url: String,
            body: String?,
            headers: Map<String, String>?): ClientResponse {
        val request = HttpPost(url)
        headers?.forEach { name, value -> request.setHeader(name, value) }
        
        if (body != null) {
            val contentType = ContentType.create(
                    headers?.get(CONTENT_TYPE_HEADER) ?: CONTENT_TYPE,
                    Consts.UTF_8)
            request.entity = StringEntity(body, contentType)
        }
        return call(stepikApiClient, request)
    }
    
    override fun get(
            stepikApiClient: StepikApiClient,
            url: String,
            headers: Map<String, String>?): ClientResponse {
        val request = HttpGet(url)
        headers?.forEach { name, value -> request.setHeader(name, value) }
        return call(stepikApiClient, request)
    }
    
    private fun call(
            stepikApiClient: StepikApiClient,
            request: HttpUriRequest): ClientResponse {
        var statusCode: Int
        var result: StringBuilder
        
        try {
            httpClient.execute(request)
                    .use { response ->
                        statusCode = response.statusLine
                                .statusCode
                        
                        val entity = response.entity
                        result = StringBuilder()
                        
                        if (entity != null) {
                            try {
                                BufferedReader(
                                        InputStreamReader(entity.content, ENCODING)).use { content ->
                                    
                                    var line = content.readLine()
                                    while (line != null) {
                                        result.append("\n")
                                                .append(line)
                                        line = content.readLine()
                                    }
                                    
                                    if (result.isNotEmpty()) {
                                        result.deleteCharAt(0) // Delete first break line
                                    }
                                }
                            } catch (e: IOException) {
                                throw StepikClientException("Failed getting a content", e)
                            } catch (e: UnsupportedOperationException) {
                                throw StepikClientException("Failed getting a content", e)
                            }
                            
                        }
                        return ClientResponse(stepikApiClient, statusCode, result.toString())
                    }
        } catch (e: IOException) {
            throw StepikClientException("Failed a request", e)
        }
    }
    
    companion object {
        private val logger = LoggerFactory.getLogger(HttpTransportClient::class.java)
        
        private const val ENCODING = "UTF-8"
        private const val CONTENT_TYPE = "application/x-www-form-urlencoded"
        private const val CONTENT_TYPE_HEADER = "Content-Type"
        
        private const val MAX_SIMULTANEOUS_CONNECTIONS = 100000
        private const val FULL_CONNECTION_TIMEOUT_S = 30
        private const val CONNECTION_TIMEOUT_MS = 5000
        private const val SOCKET_TIMEOUT_MS = FULL_CONNECTION_TIMEOUT_S * 1000
        private val instances = HashMap<Pair<String?, Int>, HttpTransportClient>()
        private var instance: HttpTransportClient? = null
        
        fun getInstance(userAgent: String): HttpTransportClient {
            if (instance == null) {
                instance = HttpTransportClient(userAgent)
            }
            
            return instance!!
        }
        
        fun getInstance(
                proxyHost: String?,
                proxyPort: Int,
                userAgent: String): HttpTransportClient {
            val proxy = Pair(proxyHost, proxyPort)
            
            return instances.computeIfAbsent(proxy) { (_, _) ->
                HttpTransportClient(proxyHost, proxyPort, userAgent)
            }
        }
    }
}
