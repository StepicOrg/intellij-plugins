package org.stepik.api.client

class ClientResponse internal constructor(
        val stepikApiClient: StepikApiClient,
        val statusCode: Int,
        val body: String) {
    
    fun <T> getBody(clazz: Class<T>): T? {
        return stepikApiClient.jsonConverter.fromJson(body, clazz)
    }
}
