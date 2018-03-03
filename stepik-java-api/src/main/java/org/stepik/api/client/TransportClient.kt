package org.stepik.api.client


interface TransportClient {
    fun post(stepikApiClient: StepikApiClient, url: String, body: String?): ClientResponse

    operator fun get(stepikApiClient: StepikApiClient, url: String): ClientResponse

    fun post(
            stepikApiClient: StepikApiClient,
            url: String,
            body: String?,
            headers: Map<String, String>?): ClientResponse

    operator fun get(
            stepikApiClient: StepikApiClient,
            url: String,
            headers: Map<String, String>?): ClientResponse
}
