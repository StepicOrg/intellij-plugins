package org.stepik.plugin.utils;

import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
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

        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        String[] paramStr = link.split("\\?");
        if (paramStr.length > 1) {
            String[] params = paramStr[1].split("&");
            final String[] unitId = {"-1"};
            Arrays.stream(params)
                    .filter(s -> s.startsWith("unit="))
                    .forEach(s -> unitId[0] = s.substring(5, s.length()));

            if (!unitId[0].equals("-1")) {
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

    @NotNull
    public static String getCourseDescription(@NotNull Course course) {
        String description;
        if (course == StepikProjectGenerator.EMPTY_COURSE) {
            description = "Wrong link";
        } else {

            StringBuilder sb = new StringBuilder();
            sb.append("<b>Course:</b> ")
                    .append(course.toString())
                    .append("<br><br>")
                    .append(course.getDescription());
            if (course.isAdaptive()) {
                sb.append("<p style='font-weight: bold;'>This course is adaptive.<br>")
                        .append("Sorry, but we don't support adaptive courses yet</p>");
            }
            description = sb.toString();
        }

        return description;
    }
}
