package org.stepik.api.objects.attempts;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class DatasetDeserializer implements JsonDeserializer<Dataset> {

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

        JsonArray options = object.getAsJsonArray("options");
        if (options != null) {
            List<String> optionsArray = new ArrayList<>();

            options.forEach(option -> optionsArray.add(option.getAsString()));

            dataset.setOptions(optionsArray.toArray(new String[optionsArray.size()]));
        }

        return dataset;
    }
}
