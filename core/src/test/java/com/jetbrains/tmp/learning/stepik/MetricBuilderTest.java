package com.jetbrains.tmp.learning.stepik;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jetbrains.tmp.learning.StudySerializationUtils;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import com.jetbrains.tmp.learning.stepik.metric.MetricActions;
import com.jetbrains.tmp.learning.stepik.metric.MetricBuilder;
import com.jetbrains.tmp.learning.stepik.metric.MetricsWrapper;
import com.jetbrains.tmp.learning.stepik.metric.PluginNames;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MetricBuilderTest {
    static final private Gson GSON =
            new GsonBuilder().registerTypeAdapter(TaskFile.class,
                    new StudySerializationUtils.Json.StepikTaskFileAdapter())
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

    @Test
    public void buildInvalid() throws Exception {
        MetricsWrapper metric = new MetricBuilder().build();
        assertFalse(metric.isCorrect());
    }

    @Test
    public void buildGetCourse() throws Exception {
        MetricsWrapper metric = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.GET_COURSE)
                .setCourseId(512)
                .build();

        String requestBody = GSON.toJson(metric);
        String answer = readTestFile("get_course.json").replaceAll("[\n\t ]+", "");
        assertEquals(requestBody, answer);
    }

    @Test
    public void buildDownload() throws Exception {
        MetricsWrapper metric = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.DOWNLOAD)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .setStepId(42)
                .build();

        String requestBody = GSON.toJson(metric);
        String answer = readTestFile("download.json").replaceAll("[\n\t ]+", "");
        assertEquals(requestBody, answer);
    }

    @Test
    public void buildPost() throws Exception {
        MetricsWrapper metric = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.POST)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .setStepId(42)
                .build();

        String requestBody = GSON.toJson(metric);
        String answer = readTestFile("post.json").replaceAll("[\n\t ]+", "");
        assertEquals(requestBody, answer);
    }


    @Test
    public void correctPost() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.POST)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .setStepId(42)
                .build();
        assertTrue(wrapper.isCorrect());
    }

    @Test
    public void correctGet() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.GET_COURSE)
                .setCourseId(187)
                .build();
        assertTrue(wrapper.isCorrect());
    }

    @Test
    public void correctDownload() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.DOWNLOAD)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .setStepId(42)
                .build();
        assertTrue(wrapper.isCorrect());
    }

    @Test
    public void invalidPostNoStepId() {
        // no step id
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.POST)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .build();
        assertFalse(wrapper.isCorrect());
    }

    @Test
    public void invalidPostNoCourseId() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.POST)
                .addTag(SupportedLanguages.JAVA)
                .setStepId(187)
                .build();
        assertFalse(wrapper.isCorrect());

    }

    @Test
    public void invalidPostNoLang() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.POST)
                .setCourseId(187)
                .setStepId(42)
                .build();
        assertFalse(wrapper.isCorrect());
    }

    @Test
    public void invalidGetHaveLang() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.GET_COURSE)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .build();
        assertFalse(wrapper.isCorrect());
    }

    @Test
    public void invalidGetNoCourseId() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.GET_COURSE)
                .build();
        assertFalse(wrapper.isCorrect());
    }

    @Test
    public void invalidGetHaveStepId() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.GET_COURSE)
                .setCourseId(187)
                .setStepId(42)
                .build();
        assertFalse(wrapper.isCorrect());
    }

    @Test
    public void invalidDownloadNoStepId() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.DOWNLOAD)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .build();
        assertFalse(wrapper.isCorrect());
    }

    @Test
    public void invalidDownloadNoCourseId() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.DOWNLOAD)
                .addTag(SupportedLanguages.JAVA)
                .setStepId(187)
                .build();
        assertFalse(wrapper.isCorrect());
    }

    @Test
    public void invalidDownloadNoLang() {
        MetricsWrapper wrapper = new MetricBuilder().addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.DOWNLOAD)
                .setCourseId(187)
                .setStepId(42)
                .build();
        assertFalse(wrapper.isCorrect());
    }

    private String readTestFile(@NotNull String fileName) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = this.getClass().getResourceAsStream("/samples/" + fileName);

        if (inputStream == null) {
            throw new FileNotFoundException("/samples/" + fileName);
        }
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append("\n");
        }

        return sb.toString();
    }
}