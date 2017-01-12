package org.stepik.api.queries;

import org.apache.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stepik.api.Utils;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.client.ClientResponse;
import org.stepik.api.client.JsonConverter;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.client.TransportClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.auth.TokenInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author meanmail
 */
abstract class StepikAbstractQuery<T> {
    private static final Logger logger = LoggerFactory.getLogger(StepikAbstractQuery.class);

    private final StepikAbstractAction stepikAction;
    private final Class<T> responseClass;

    private final QueryMethod method;
    private final Map<String, String[]> params = new HashMap<>();

    StepikAbstractQuery(
            @NotNull StepikAbstractAction stepikAction,
            @NotNull Class<T> responseClass,
            @NotNull QueryMethod method) {
        this.stepikAction = stepikAction;
        this.responseClass = responseClass;
        this.method = method;
    }

    @NotNull
    protected StepikAbstractAction getStepikAction() {
        return stepikAction;
    }

    protected void addParam(@NotNull String key, @NotNull String value) {
        params.put(key, new String[]{value});
    }

    protected void addParam(@NotNull String key, boolean value) {
        params.put(key, new String[]{String.valueOf(value)});
    }

    protected void addParam(@NotNull String key, @NotNull Long... values) {
        String[] paramValues = Arrays.stream(values)
                .map(String::valueOf)
                .collect(Collectors.toList())
                .toArray(new String[values.length]);
        params.put(key, paramValues);
    }

    protected void addParam(@NotNull String key, @NotNull Integer... values) {
        String[] paramValues = Arrays.stream(values)
                .map(String::valueOf)
                .collect(Collectors.toList())
                .toArray(new String[values.length]);
        params.put(key, paramValues);
    }

    protected void addParam(@NotNull String key, @NotNull List<Long> values) {
        String[] paramValues = values.stream()
                .map(String::valueOf)
                .collect(Collectors.toList())
                .toArray(new String[values.size()]);
        params.put(key, paramValues);
    }

    protected void addParam(@NotNull String key, @NotNull long[] values) {
        String[] paramValues = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            paramValues[i] = String.valueOf(values[i]);
        }

        params.put(key, paramValues);
    }

    @NotNull
    protected abstract String getUrl();

    @NotNull
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
                    response = transportClient.get(stepikApi, url, headers);
                    break;
                case POST:
                    response = transportClient.post(stepikApi, url, getBody(), headers);
                    break;
            }
        } catch (IOException e) {
            String message = "Failed query to " + getUrl();
            logger.error(message, e);
            throw new StepikClientException(message, e);
        }

        if (response.getStatusCode() / 100 != 2) {
            String message = "Failed query to " + getUrl() + " returned the status code " + response.getStatusCode();
            logger.error(message);
            throw new StepikClientException(message);
        }

        T result = response.getBody(responseClass);

        if (responseClass == VoidResult.class) {
            //noinspection unchecked
            return (T) new VoidResult();
        }
        if (result == null) {
            throw new StepikClientException("Request successfully but the response body is null: " + url);
        }
        return result;
    }

    @NotNull
    protected String getContentType() {
        return "application/json";
    }

    @NotNull
    protected String getBody() {
        return mapToGetString();
    }

    @NotNull
    private String mapToGetString() {
        return params.entrySet().stream()
                .map(entry -> Utils.mapToGetString(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    @NotNull
    public JsonConverter getJsonConverter() {
        return getStepikAction().getStepikApiClient().getJsonConverter();
    }
}
