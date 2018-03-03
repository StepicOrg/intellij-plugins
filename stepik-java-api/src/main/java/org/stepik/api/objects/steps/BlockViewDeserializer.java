package org.stepik.api.objects.steps;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.stepik.api.Utils;

import java.lang.reflect.Type;

public class BlockViewDeserializer implements JsonDeserializer<BlockView> {

    @Override
    public BlockView deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        if (!(json instanceof JsonObject)) {
            return null;
        }
        BlockView block = new BlockView();
        JsonObject object = json.getAsJsonObject();

        block.setName(object.get("name").getAsString());
        JsonElement textMember = object.get("text");

        if (textMember != null) {
            String text = Utils.INSTANCE.cleanString(textMember.getAsString());
            block.setText(text);
        }

        block.setVideo(context.deserialize(object.get("video"), Video.class));
        block.setAnimation(context.deserialize(object.get("animation"), Object.class));
        block.setOptions(context.deserialize(object.get("options"), BlockViewOptions.class));
        block.setSubtitleFiles(Utils.INSTANCE.getStringList(object, "subtitle_files"));

        return block;
    }
}
