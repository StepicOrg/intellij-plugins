package org.stepik.api.objects.attempts;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

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
        JsonArray jsonArray = object.getAsJsonArray(memberName);
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

        JsonPrimitive multipleChoice = object.getAsJsonPrimitive("is_multiple_choice");
        if (multipleChoice != null) {
            dataset.setMultipleChoice(multipleChoice.getAsBoolean());
        }

        JsonPrimitive textDisabled = object.getAsJsonPrimitive("is_text_disabled");
        if (textDisabled != null) {
            dataset.setTextDisabled(textDisabled.getAsBoolean());
        }

        JsonArray options = object.getAsJsonArray("options");
        if (options != null) {
            List<String> optionsArray = new ArrayList<>();
            options.forEach(option -> optionsArray.add(option.getAsString()));
            dataset.setOptions(optionsArray.toArray(new String[optionsArray.size()]));
        }

        JsonArray pairs = object.getAsJsonArray("pairs");
        if (pairs != null) {
            List<Pair> array = new ArrayList<>();
            pairs.forEach(pair -> array.add(context.deserialize(pair, Pair.class)));
            dataset.setPairs(array);
        }

        dataset.setRows(getStringList(object, "rows"));
        dataset.setColumns(getStringList(object, "columns"));

        JsonPrimitive isCheckbox = object.getAsJsonPrimitive("is_checkbox");
        if (isCheckbox != null) {
            dataset.setCheckbox(isCheckbox.getAsBoolean());
        }

        JsonPrimitive description = object.getAsJsonPrimitive("description");
        if (description != null) {
            dataset.setDescription(description.getAsString());
        }

        JsonArray components = object.getAsJsonArray("components");
        if (components != null) {
            List<Component> array = new ArrayList<>();
            components.forEach(component -> array.add(context.deserialize(component, Component.class)));
            dataset.setComponents(array);
        }

        return dataset;
    }
}
