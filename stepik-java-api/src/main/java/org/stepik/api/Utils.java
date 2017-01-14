package org.stepik.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author meanmail
 */
public class Utils {

    public static String mapToGetString(@NotNull String name, @NotNull String[] values) {
        return mapToGetString(name, values, "UTF-8");
    }

    @NotNull
    public static String mapToGetString(@NotNull String name, @NotNull String[] values, @NotNull String enc) {
        String encodedName = encode(name, enc);
        return Arrays.stream(values)
                .map(value -> encodedName + "=" + encode(value, enc))
                .collect(Collectors.joining("&"));
    }

    @NotNull
    private static String encode(@NotNull String value, @NotNull String enc) {
        try {
            return URLEncoder.encode(value, enc);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    @Nullable
    public static String readFile(@NotNull Path file) {
        Optional<String> content;
        try {
            content = Files.readAllLines(file)
                    .stream()
                    .reduce((line, text) -> text + line);
        } catch (IOException e) {
            return null;
        }

        return content.orElse(null);
    }
}
