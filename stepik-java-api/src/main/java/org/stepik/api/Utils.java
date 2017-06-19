package org.stepik.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
                .filter(Objects::nonNull)
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

    @Nullable
    public static List<String> getStringList(@NotNull JsonObject object, @NotNull String fieldName) {
        return getList(object, fieldName, JsonElement::getAsString);
    }

    @Nullable
    public static <T> List<T> getList(
            @NotNull JsonObject object,
            @NotNull String fieldName,
            @NotNull Function<JsonElement, T> getter) {
        if (!object.has(fieldName) || !object.get(fieldName).isJsonArray()) {
            return null;
        }
        JsonArray array = getJsonArray(object, fieldName);
        if (array != null) {
            List<T> list = new ArrayList<>();
            array.forEach(item -> list.add(getter.apply(item)));
            return list;
        }

        return null;
    }

    @Nullable
    public static JsonArray getJsonArray(@NotNull JsonObject json, @NotNull String memberName) {
        if (!json.has(memberName) || !json.get(memberName).isJsonArray()) {
            return null;
        }

        return json.getAsJsonArray(memberName);
    }

    @Nullable
    private static JsonPrimitive getPrimitive(@NotNull JsonObject json, @NotNull String memberName) {
        if (!json.has(memberName)) {
            return null;
        }
        JsonElement element = json.get(memberName);

        if (!element.isJsonPrimitive()) {
            return null;
        }

        return element.getAsJsonPrimitive();
    }

    @Nullable
    public static String getString(@NotNull JsonObject json, @NotNull String memberName) {
        JsonPrimitive primitive = getPrimitive(json, memberName);

        if (primitive == null) {
            return null;
        }

        if (primitive.isString()) {
            return primitive.getAsString();
        }

        return null;
    }

    @Nullable
    public static Boolean getBoolean(@NotNull JsonObject json, @NotNull String memberName) {
        JsonPrimitive primitive = getPrimitive(json, memberName);

        if (primitive == null) {
            return null;
        }

        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        }

        return null;
    }

    @NotNull
    public static String cleanString(@NotNull String string) {
        return string.replaceAll("[\\u0000-\\u0008\\u000b\\u000c\\u000e-\\u001f]", "");
    }
}
