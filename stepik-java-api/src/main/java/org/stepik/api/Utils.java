package org.stepik.api;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author meanmail
 */
public class Utils {
    @NotNull
    public static String mapToGetString(@NotNull String name, @NotNull String[] values) {
        String encodedName = encode(name);
        return Arrays.stream(values)
                .map(value -> encodedName + "=" + encode(value))
                .collect(Collectors.joining("&"));
    }

    @NotNull
    private static String encode(@NotNull String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
