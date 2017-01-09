package org.stepik.api.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public interface JsonConverter {
    @Nullable
    <T> T fromJson(@NotNull String json, @NotNull Class<T> clazz);

    @NotNull
    String toJson(@Nullable Object object);
}
