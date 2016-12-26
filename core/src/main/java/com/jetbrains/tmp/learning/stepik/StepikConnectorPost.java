/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.tmp.learning.stepik;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.tmp.learning.stepik.entities.SubmissionContainer;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class StepikConnectorPost {
    private static final Logger logger = Logger.getInstance(StepikConnectorPost.class.getName());
    static final private Gson GSON =
            new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    // TODO All methods must be rewrite as it
    private static void postToStepik(String link, AbstractHttpEntity entity) throws IOException {
        final HttpPost request = new HttpPost(EduStepikNames.STEPIK_API_URL + link);
        request.setEntity(entity);

        final CloseableHttpResponse response = StepikConnectorLogin.getHttpClient().execute(request);
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity responseEntity = response.getEntity();
        final String responseString = responseEntity != null ? EntityUtils.toString(responseEntity) : "";
        if (statusLine.getStatusCode() / 100 != 2) {
            throw new IOException("Stepik returned " + statusLine.getStatusCode() + " status code " + responseString);
        }
    }

    private static <T> T postToStepik(String link, final Class<T> container, String requestBody) throws IOException {
        final HttpPost request = new HttpPost(EduStepikNames.STEPIK_API_URL + link);
        request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

        final CloseableHttpResponse response = StepikConnectorLogin.getHttpClient().execute(request);
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity responseEntity = response.getEntity();
        final String responseString = responseEntity != null ? EntityUtils.toString(responseEntity) : "";
        if (statusLine.getStatusCode() / 100 != 2) {
            throw new IOException("Stepik returned " + statusLine.getStatusCode() + " status code " + responseString);
        }
        return GSON.fromJson(responseString, container);
    }

    private static void postToStepikVoid(String link, String requestBody) throws IOException {
        final HttpPost request = new HttpPost(EduStepikNames.STEPIK_API_URL + link);
        request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

        final CloseableHttpResponse response = StepikConnectorLogin.getHttpClient().execute(request);
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity responseEntity = response.getEntity();
        final String responseString = responseEntity != null ? EntityUtils.toString(responseEntity) : "";
        if (statusLine.getStatusCode() / 100 != 2) {
            throw new IOException("Stepik returned " + statusLine.getStatusCode() + " status code " + responseString);
        }
    }

    public static StepikWrappers.AttemptContainer getAttempt(int stepId) throws IOException {
        String requestBody = new Gson().toJson(new StepikWrappers.AttemptWrapper(stepId));
        try {
            return postToStepik(EduStepikNames.ATTEMPTS, StepikWrappers.AttemptContainer.class, requestBody);
        } catch (IOException e) {
            logger.warn("Can not get Attempt\n" + e.toString());
            throw new IOException(e);
        }
    }

    @Deprecated
    public static SubmissionContainer postSubmission(String text, String attemptId) {
        String requestBody = new Gson().toJson(new StepikWrappers.SubmissionToPostWrapper(attemptId, "java8", text));
        try {
            return postToStepik(EduStepikNames.SUBMISSIONS, SubmissionContainer.class, requestBody);
        } catch (IOException e) {
            logger.warn("Can not post Submission\n" + e.toString());
            return null;
        }
    }

    public static SubmissionContainer postSubmission(
            StepikWrappers.SubmissionToPostWrapper submissionToPostWrapper) {
        String requestBody = new Gson().toJson(submissionToPostWrapper);
        try {
            return postToStepik(EduStepikNames.SUBMISSIONS, SubmissionContainer.class, requestBody);
        } catch (IOException e) {
            logger.warn("Can not post Submission\n" + e.toString());
            return null;
        }
    }

    public static boolean enrollToCourse(final int courseId) {
        final StepikWrappers.EnrollmentWrapper enrollment = new StepikWrappers.EnrollmentWrapper(String.valueOf(courseId));
        try {
            StringEntity entity = new StringEntity(new GsonBuilder().create().toJson(enrollment));
            postToStepik(EduStepikNames.ENROLLMENTS, entity);
            return true;
        } catch (IOException e) {
            logger.warn("EnrollToCourse error\n" + e.getMessage());
        }
        return false;
    }

    public static void postMetric(StepikWrappers.MetricsWrapper metric) {
        String requestBody = GSON.toJson(metric);
        logger.info(requestBody);
        try {
            postToStepikVoid(EduStepikNames.METRICS, requestBody);
        } catch (IOException e) {
            logger.warn("Can't post a metric\n" + e.toString());
        }
    }

}
