package org.stepik.plugin.utils;

import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.units.Units;

import java.util.Arrays;
import java.util.List;

/**
 * @author meanmail
 */
public class Utils {
    public static int getCourseIdFromLink(@NotNull String link) {
        link = link.trim();
        if (link.isEmpty()) {
            return 0;
        }

        if (isFillOfInt(link)) {
            return Integer.parseInt(link);
        }

        List<String> list = Arrays.asList(link.split("/"));
        int i = list.indexOf("course");
        if (i != -1) {
            if (i + 1 == list.size())
                return 0;
            String[] parts = list.get(i + 1).split("-");
            return parts.length != 0 ? Integer.parseInt(parts[parts.length - 1]) : 0;
        }

        String[] paramStr = link.split("\\?");
        if (paramStr.length > 1) {
            String[] params = paramStr[1].split("&");
            final String[] unitId = {"-1"};
            Arrays.stream(params)
                    .filter(s -> s.startsWith("unit="))
                    .forEach(s -> unitId[0] = s.substring(5, s.length()));

            if (!unitId[0].equals("-1")) {
                StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
                Units units = stepikApiClient.units()
                        .get()
                        .id(Integer.parseInt(unitId[0]))
                        .execute();

                return getCourseId(units);
            }
        }

        list = Arrays.asList(link.split("/"));
        i = list.indexOf("lesson");
        if (i != -1) {
            if (i + 1 == list.size())
                return 0;
            String[] parts = list.get(i + 1).split("-");
            int lessonId = Integer.parseInt(parts[parts.length - 1]);
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
            Units units = stepikApiClient.units()
                    .get()
                    .lesson(lessonId)
                    .execute();

            return getCourseId(units);
        }
        return 0;
    }

    private static int getCourseId(@NotNull Units units) {
        if (units.isEmpty()) {
            return 0;
        }
        int sectionId = units.getUnits().get(0).getSection();
        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        Sections sections = stepikApiClient.sections()
                .get()
                .id(sectionId)
                .execute();

        return sections.isEmpty() ? 0 : sections.getSections().get(0).getCourse();
    }

    private static boolean isFillOfInt(@NotNull String link) {
        return link.matches("[0-9]+");
    }

    @Language("HTML")
    private static final String DEFAULT_DESCRIPTION =
            "<b>A course does not selected.</b><br>" +
                    "<ul>" +
                    "<li>Select a course from a list.</li>" +
                    "<li>Push on a refresh button if a course list is a empty.</li>" +
                    "<li>Write a link to a course (example, https://stepik.org/187/) or a id of course.</li>" +
                    "</ul>";

    @Language("HTML")
    private static final String DEFAULT_MESSAGE_FOR_ADAPTIVE =
            "<p style='font-weight: bold;'>This course is adaptive.<br>" +
                    "Sorry, but we don't support adaptive courses yet</p>";

    @NotNull
    public static String getCourseDescription(@NotNull Course course) {
        if (course.getId() == 0) {
            return DEFAULT_DESCRIPTION;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(course.getDescription());
            if (course.isAdaptive()) {
                sb.append(DEFAULT_MESSAGE_FOR_ADAPTIVE);
            }
            return sb.toString();
        }
    }
}
