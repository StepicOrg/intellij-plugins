package org.stepik.api.objects.submissions;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.stepik.api.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class ReplyDeserializer implements JsonDeserializer<Reply> {

    @Override
    public Reply deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || !(json instanceof JsonObject)) {
            return null;
        }

        Reply reply = new Reply();
        JsonObject object = json.getAsJsonObject();

        String language = Utils.INSTANCE.getString(object, "language");
        reply.setLanguage(language);

        String code = Utils.INSTANCE.getString(object, "code");
        reply.setCode(code);

        String formula = Utils.INSTANCE.getString(object, "formula");
        reply.setFormula(formula);

        String text = Utils.INSTANCE.getString(object, "text");
        reply.setText(text);

        String number = Utils.INSTANCE.getString(object, "number");
        reply.setNumber(number);

        JsonArray ordering = Utils.INSTANCE.getJsonArray(object, "ordering");
        if (ordering != null) {
            List<Integer> intOrdering = new ArrayList<>();
            ordering.forEach(item -> intOrdering.add(item.getAsInt()));
            reply.setOrdering(intOrdering);
        }

        reply.setAttachments(Utils.INSTANCE.getList(object,
                "attachments",
                (jsonElement) -> context.deserialize(jsonElement, Attachment.class)));
        reply.setFiles(Utils.INSTANCE.getStringList(object, "files"));

        JsonArray choices = Utils.INSTANCE.getJsonArray(object, "choices");
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

        reply.setBlanks(Utils.INSTANCE.getStringList(object, "blanks"));

        return reply;
    }
}
