package org.stepik.api.objects.steps;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.stepik.api.objects.steps.queezes.BlockViewOptions;

/**
 * @author meanmail
 */
public class BlockView {
    private String name;
    private String text;
    private String video;
    private String animation;
    private String options;
    @SerializedName("subtitle_files")
    private String[] subtitleFiles;

    public String getName() {
        return name;
    }

    public <T extends BlockViewOptions> T getOptions(Class<T> clazz) {
        return new Gson().fromJson(options, clazz);
    }

    public String getText() {
        return text;
    }
}
