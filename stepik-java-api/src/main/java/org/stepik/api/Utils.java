package org.stepik.api;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author meanmail
 */
public class Utils {
    public static String mapToGetString(String name, String[] values) {
        return Arrays.stream(values)
                .map(value -> name + "=" + value)
                .collect(Collectors.joining("&"));
    }
}
