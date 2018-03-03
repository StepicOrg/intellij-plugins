package org.stepik.api.objects.attempts;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author meanmail
 */
public class DatasetDeserializer implements JsonDeserializer<Dataset> {

    @NotNull
    private static List<String> getStringList(@NotNull JsonObject object, @NotNull String memberName) {
        JsonArray jsonArray = Utils.INSTANCE.getJsonArray(object, memberName);
        if (jsonArray != null) {
            List<String> array = new ArrayList<>();
            jsonArray.forEach(element -> array.add(element.getAsString()));
            return array;
        }
        return Collections.emptyList();
    }

    @Override
    public Dataset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || !(json instanceof JsonObject)) {
            return null;
        }

        Dataset dataset = new Dataset();
        JsonObject object = json.getAsJsonObject();

        Boolean multipleChoice = Utils.INSTANCE.getBoolean(object, "is_multiple_choice");
        dataset.setMultipleChoice(multipleChoice);

        Boolean textDisabled = Utils.INSTANCE.getBoolean(object, "is_text_disabled");
        dataset.setTextDisabled(textDisabled);

        dataset.setOptions(getStringList(object, "options"));

        JsonArray pairs = Utils.INSTANCE.getJsonArray(object, "pairs");
        if (pairs != null) {
            List<StringPair> array = new ArrayList<>();
            pairs.forEach(pair -> array.add(context.deserialize(pair, StringPair.class)));
            dataset.setPairs(array);
        }

        dataset.setRows(getStringList(object, "rows"));
        dataset.setColumns(getStringList(object, "columns"));

        Boolean isCheckbox = Utils.INSTANCE.getBoolean(object, "is_checkbox");
        dataset.setCheckbox(isCheckbox);

        String description = Utils.INSTANCE.getString(object, "description");
        dataset.setDescription(description);

        JsonArray components = Utils.INSTANCE.getJsonArray(object, "components");
        if (components != null) {
            List<Component> array = new ArrayList<>();
            components.forEach(component -> array.add(context.deserialize(component, Component.class)));
            dataset.setComponents(array);
        }

        return dataset;
    }
}
