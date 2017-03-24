package org.stepik.api.actions;

import org.apache.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stepik.api.client.ClientResponse;
import org.stepik.api.client.StatusCodes;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.client.TransportClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.exceptions.StepikUnauthorizedException;
import org.stepik.api.objects.auth.TokenInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public class StepikFilesAction extends StepikAbstractAction {
    private static final Logger logger = LoggerFactory.getLogger(StepikFilesAction.class);

    public StepikFilesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public String get(@NotNull String url, String contentType) {
        StepikApiClient stepikApi = getStepikApiClient();
        TransportClient transportClient = stepikApi.getTransportClient();

        Map<String, String> headers = new HashMap<>();
        TokenInfo tokenInfo = stepikApi.getTokenInfo();
        String accessToken = tokenInfo.getAccessToken();
        if (accessToken != null) {
            String tokenType = tokenInfo.getTokenType();
            headers.put(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken);
        }
        headers.put(HttpHeaders.CONTENT_TYPE, contentType);

        ClientResponse response = transportClient.get(stepikApi, url, headers);

        if (response.getStatusCode() / 100 != 2) {
            String message = "Failed query to " + url + " returned the status code " + response.getStatusCode();
            logger.warn(message);

            if (response.getStatusCode() == StatusCodes.SC_UNAUTHORIZED) {
                throw new StepikUnauthorizedException(message);
            } else {
                throw new StepikClientException(message);
            }
        }

        String result = response.getBody();

        if (result == null) {
            throw new StepikClientException("Request successfully but the response body is null: " + url);
        }
        return result;
    }
}
