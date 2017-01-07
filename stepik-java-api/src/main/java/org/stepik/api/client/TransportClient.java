package org.stepik.api.client;

import java.io.IOException;
import java.util.Map;

/**
 * @author meanmail
 */
public interface TransportClient {
    ClientResponse post(StepikApiClient stepikApiClient, String url, String body) throws IOException;

    ClientResponse get(StepikApiClient stepikApiClient, String url) throws IOException;

    ClientResponse post(StepikApiClient stepikApiClient, String url, String body, Map<String, String> headers)
            throws IOException;

    ClientResponse get(StepikApiClient stepikApiClient, String url, Map<String, String> headers) throws IOException;
}
