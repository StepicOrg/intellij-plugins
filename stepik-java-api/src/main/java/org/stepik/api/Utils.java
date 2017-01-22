package org.stepik.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author meanmail
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
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
            return URLEncoder.encode(value, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    @Nullable
    public static String readFile(@NotNull Path file) {
        Optional<String> content;
        try {
            content = Files.readAllLines(file, UTF_8)
                    .stream()
                    .reduce((line, text) -> text + line);
        } catch (IOException e) {
            logger.warn("Failed reading a file: {}\n{}", file, e);
            return null;
        }

        return content.orElse(null);
    }
}
