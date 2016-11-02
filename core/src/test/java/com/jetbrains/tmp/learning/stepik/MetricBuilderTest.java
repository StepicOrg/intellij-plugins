package com.jetbrains.tmp.learning.stepik;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jetbrains.tmp.learning.LangManager;
import com.jetbrains.tmp.learning.StudySerializationUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class MetricBuilderTest {
    static final private Gson GSON =
            new GsonBuilder().registerTypeAdapter(TaskFile.class,
                    new StudySerializationUtils.Json.StepikTaskFileAdapter())
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

    @Test
    public void buildInvalid() throws Exception {
        MetricBuilder.MetricsWrapper metric = MetricBuilder.getInstance().build();
        assertEquals(EduNames.INVALID, metric.metric.name);
    }

    @Test
    public void buildGetCourse() throws Exception {
        MetricBuilder.MetricsWrapper metric = MetricBuilder.getInstance()
                .addTag(MetricBuilder.PluginNames.STEPIK_UNION)
                .addTag(MetricBuilder.MetricActions.GET_COURSE)
                .setCourseId(512)
                .build();

        String requestBody = GSON.toJson(metric);
        String answer = readTestFile("get_course.json").replaceAll("[\n\t ]+","") ;
        assertEquals(requestBody, answer);
    }

    @Test
    public void buildDownload() throws Exception {
        MetricBuilder.MetricsWrapper metric = MetricBuilder.getInstance()
                .addTag(MetricBuilder.PluginNames.STEPIK_UNION)
                .addTag(MetricBuilder.MetricActions.DOWNLOAD)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .setStepId(42)
                .build();

        String requestBody = GSON.toJson(metric);
        String answer = readTestFile("download.json").replaceAll("[\n\t ]+","") ;
        assertEquals(requestBody, answer);
    }

    @Test
    public void buildPost() throws Exception {
        MetricBuilder.MetricsWrapper metric = MetricBuilder.getInstance()
                .addTag(MetricBuilder.PluginNames.STEPIK_UNION)
                .addTag(MetricBuilder.MetricActions.POST)
                .addTag(SupportedLanguages.JAVA)
                .setCourseId(187)
                .setStepId(42)
                .build();

        String requestBody = GSON.toJson(metric);
        String answer = readTestFile("post.json").replaceAll("[\n\t ]+","") ;
        assertEquals(requestBody, answer);
    }


    @Test
    public void check() {
        // TODO add tests on "check()" in MetricBuilder
    }

    private String readTestFile(@NotNull String fileName) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = this.getClass().getResourceAsStream("/samples/" + fileName);

        if (inputStream == null) return null;
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append("\n");
        }

        return sb.toString();
    }
}