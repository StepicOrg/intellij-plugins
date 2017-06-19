package org.stepik.api.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stepik.api.objects.attempts.Dataset;
import org.stepik.api.objects.attempts.DatasetDeserializer;
import org.stepik.api.objects.steps.BlockView;
import org.stepik.api.objects.steps.BlockViewDeserializer;
import org.stepik.api.objects.submissions.Reply;
import org.stepik.api.objects.submissions.ReplyDeserializer;

/**
 * @author meanmail
 */
public class DefaultJsonConverter implements JsonConverter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultJsonConverter.class);
    private static JsonConverter instance;
    private final Gson gson;

    private DefaultJsonConverter() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Dataset.class, new DatasetDeserializer())
                .registerTypeAdapter(Reply.class, new ReplyDeserializer())
                .registerTypeAdapter(BlockView.class, new BlockViewDeserializer())
                .create();
    }

    @NotNull
    public static JsonConverter getInstance() {
        if (instance == null) {
            instance = new DefaultJsonConverter();
        }
        return instance;
    }

    @Nullable
    @Override
    public <T> T fromJson(@Nullable String json, @NotNull Class<T> clazz) {
        if (json == null) {
            return null;
        }

        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            logger.warn(String.format("Failed %s fromJson %s ", clazz.getSimpleName(), json), e);
            return null;
        }
    }

    @NotNull
    @Override
    public String toJson(@Nullable Object object) {
        return gson.toJson(object);
    }
}
