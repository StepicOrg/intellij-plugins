package org.stepik.api.objects.submissions;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author meanmail
 */
public class ReplyDeserializer implements JsonDeserializer<Reply> {

    @Nullable
    private static List<String> getStringList(@NotNull JsonObject object, @NotNull String fieldName) {
        return getList(object, fieldName, JsonElement::getAsString);
    }

    @Nullable
    private static <T> List<T> getList(
            @NotNull JsonObject object,
            @NotNull String fieldName,
            @NotNull Function<JsonElement, T> getter) {
        JsonArray array = object.getAsJsonArray(fieldName);
        if (array != null) {
            List<T> list = new ArrayList<>();
            array.forEach(item -> list.add(getter.apply(item)));
            return list;
        }

        return null;
    }

    @Override
    public Reply deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || !(json instanceof JsonObject)) {
            return null;
        }

        Reply reply = new Reply();
        JsonObject object = json.getAsJsonObject();

        JsonPrimitive language = object.getAsJsonPrimitive("language");
        if (language != null) {
            reply.setLanguage(language.getAsString());
        }

        JsonPrimitive code = object.getAsJsonPrimitive("code");
        if (code != null) {
            reply.setCode(code.getAsString());
        }

        JsonPrimitive formula = object.getAsJsonPrimitive("formula");
        if (formula != null) {
            reply.setFormula(formula.getAsString());
        }

        JsonPrimitive text = object.getAsJsonPrimitive("text");
        if (text != null) {
            reply.setText(text.getAsString());
        }

        JsonPrimitive number = object.getAsJsonPrimitive("number");
        if (number != null) {
            reply.setNumber(number.getAsString());
        }

        JsonArray ordering = object.getAsJsonArray("ordering");
        if (ordering != null) {
            List<Integer> intOrdering = new ArrayList<>();
            ordering.forEach(item -> intOrdering.add(item.getAsInt()));
            reply.setOrdering(intOrdering);
        }

        reply.setAttachments(getStringList(object, "attachments"));
        reply.setFiles(getStringList(object, "files"));

        JsonArray choices = object.getAsJsonArray("choices");
        if (choices != null && choices.size() > 0) {
            if (choices.get(0).isJsonPrimitive()) {
                List<Boolean> list = new ArrayList<>();
                choices.forEach(item -> list.add(item.getAsBoolean()));
                reply.setChoices(list);
            } else {
                List<Choice> list = new ArrayList<>();
                choices.forEach(item -> list.add(context.deserialize(item, Choice.class)));
                reply.setChoices(list);
            }
        }

        reply.setBlanks(getStringList(object, "blanks"));

        return reply;
    }
}
