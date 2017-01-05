package org.stepik.api.queries;

import com.sun.istack.internal.NotNull;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.stepik.api.Utils;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.client.ClientResponse;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.client.TransportClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.auth.TokenInfo;

import javax.activation.MimeType;
import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.stepik.api.client.StatusCodes.SC_OK;

/**
 * @author meanmail
 */
abstract class StepikAbstractQuery<T> {
    private static final Logger logger = LogManager.getLogger(StepikAbstractQuery.class);

    private final StepikAbstractAction stepikAction;
    private final Class<T> responseClass;
    @NotNull
    private final QueryMethod method;
    private Map<String, String[]> params = new HashMap<>();

    StepikAbstractQuery(StepikAbstractAction stepikAction, Class<T> responseClass, QueryMethod method) {
        this.stepikAction = stepikAction;
        this.responseClass = responseClass;
        this.method = method;
    }

    protected StepikAbstractAction getStepikAction() {
        return stepikAction;
    }

    protected void addParam(String key, String value) {
        params.put(key, new String[]{value});
    }

    protected void addParam(String key, boolean value) {
        params.put(key, new String[]{String.valueOf(value)});
    }

    protected void addParam(String key, Integer... values) {
        String[] paramValues = Arrays.stream(values)
                .map(String::valueOf)
                .collect(Collectors.toList())
                .toArray(new String[0]);
        params.put(key, paramValues);
    }

    @NotNull
    protected abstract String getUrl();

    public T execute() {
        StepikApiClient stepikApi = stepikAction.getStepikApiClient();
        TransportClient transportClient = stepikApi.getTransportClient();

        String url = getUrl();

        Map<String, String> headers = new HashMap<>();
        TokenInfo tokenInfo = stepikApi.getTokenInfo();
        String accessToken = tokenInfo.getAccessToken();
        String tokenType = tokenInfo.getTokenType();
        if (accessToken != null) {
            headers.put(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken);
        }
        headers.put(HttpHeaders.CONTENT_TYPE, getContentType());


        ClientResponse response = null;
        try {
            switch (method) {
                case GET:
                    url += "?" + mapToGetString();
                    response = transportClient.get(url, headers);
                    break;
                case POST:
                    response = transportClient.post(url, getBody(), headers);
                    break;
            }
        } catch (IOException e) {
            String message = "Failed query to " + getUrl();
            logger.error(message, e);
            throw new StepikClientException(message, e);
        }

        if (response.getStatusCode() != SC_OK) {
            String message = "Failed query to " + getUrl() + " returned the status code " + response.getStatusCode();
            logger.error(message);
            throw new StepikClientException(message);
        }

        return response.getBody(responseClass);
    }

    protected String getContentType() {
        return "application/json";
    }

    protected String getBody() {
        return mapToGetString();
    }

    private String mapToGetString() {
        return params.entrySet().stream()
                .map(entry -> Utils.mapToGetString(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }
}
