package org.stepik.api.actions

import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory
import org.stepik.api.client.StatusCodes
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.exceptions.StepikUnauthorizedException
import java.util.*

class StepikFilesAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    operator fun get(url: String, contentType: String): String {
        val stepikApi = stepikApiClient
        val transportClient = stepikApi.transportClient
        
        val headers = HashMap<String, String>()
        val tokenInfo = stepikApi.tokenInfo
        val accessToken = tokenInfo?.accessToken
        if (accessToken != null) {
            val tokenType = tokenInfo.tokenType
            headers[HttpHeaders.AUTHORIZATION] = "$tokenType $accessToken"
        }
        headers[HttpHeaders.CONTENT_TYPE] = contentType
        
        val response = transportClient[stepikApi, url, headers]
        
        if (response.statusCode / 100 != 2) {
            val message = "Failed query to $url returned the status code ${response.statusCode}"
            logger.warn(message)
            
            if (response.statusCode == StatusCodes.SC_UNAUTHORIZED) {
                throw StepikUnauthorizedException(message)
            } else {
                throw StepikClientException(message)
            }
        }
        
        return response.body
    }
    
    companion object {
        private val logger = LoggerFactory.getLogger(StepikFilesAction::class.java)
    }
}
