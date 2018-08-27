package org.stepik.api.queries

import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory
import org.stepik.api.Utils
import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.client.ClientResponse
import org.stepik.api.client.StatusCodes
import org.stepik.api.client.serialization.JsonConverter
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.exceptions.StepikUnauthorizedException
import java.util.concurrent.CompletableFuture

abstract class StepikAbstractQuery<T> internal constructor(
        protected val stepikAction: StepikAbstractAction,
        protected val responseClass: Class<T>,
        private val method: QueryMethod) {
    
    private val params = mutableMapOf<String, List<String>>()
    
    protected abstract val url: String
    
    protected abstract val contentType: String
    
    protected open val body: String
        get() = mapToGetString()
    
    val jsonConverter: JsonConverter
        get() = stepikAction.stepikApiClient.jsonConverter
    
    protected fun addParam(key: String, value: String) {
        params[key] = listOf(value)
    }
    
    protected fun addParam(key: String, vararg values: String) {
        params[key] = values.toList()
    }
    
    protected fun addParam(key: String, value: Boolean) {
        params[key] = listOf(value.toString())
    }
    
    protected fun addParam(key: String, vararg values: Long) {
        params[key] = values.map { it.toString() }
    }
    
    protected fun addParam(key: String, vararg values: Int) {
        params[key] = values.map { it.toString() }
    }
    
    protected fun <V> addParam(key: String, values: List<V>) {
        params[key] = values.map { it.toString() }
    }
    
    protected fun getParam(key: String): List<String> {
        return params.getOrDefault(key, listOf())
    }
    
    fun executeAsync(): CompletableFuture<T> {
        return CompletableFuture.supplyAsync { this.execute() }
    }
    
    open fun execute(): T {
        val stepikApi = stepikAction.stepikApiClient
        val transportClient = stepikApi.transportClient
        
        var url = url
        
        val headers = mutableMapOf<String, String>()
        val tokenInfo = stepikApi.tokenInfo
        val accessToken = tokenInfo?.accessToken
        if (accessToken != null) {
            val tokenType = tokenInfo.tokenType
            headers[HttpHeaders.AUTHORIZATION] = "$tokenType $accessToken"
        }
        headers[HttpHeaders.CONTENT_TYPE] = contentType
        
        var response: ClientResponse? = null
        when (method) {
            QueryMethod.GET  -> {
                val ids = params[IDS_KEY]
                if (ids?.size == 1) {
                    if (!url.endsWith("/")) {
                        url += "/"
                    }
                    url += ids[0]
                    params.remove(IDS_KEY)
                }
                if (!params.isEmpty()) {
                    url += "?${mapToGetString()}"
                }
                response = transportClient[stepikApi, url, headers]
            }
            QueryMethod.POST -> response = transportClient.post(stepikApi, url, body, headers)
        }
        
        if (response.statusCode / 100 != 2) {
            val message = "Failed query to $url returned the status code ${response.statusCode}"
            logger.warn(message)
            
            if (response.statusCode == StatusCodes.SC_UNAUTHORIZED) {
                throw StepikUnauthorizedException(message)
            } else {
                throw StepikClientException(message)
            }
        }
        
        val result = response.getBody(responseClass)
        
        if (responseClass === VoidResult::class.java) {
            return VoidResult() as T
        }
        if (result == null) {
            throw StepikClientException("Request successfully but the response body is null: $url")
        }
        return result
    }
    
    private fun mapToGetString(): String {
        return params.entries.joinToString("&") { entry ->
            Utils.mapToGetString(entry.key, entry.value)
        }
    }
    
    companion object {
        private val logger = LoggerFactory.getLogger(StepikAbstractQuery::class.java)
        protected const val IDS_KEY = "ids[]"
    }
}
