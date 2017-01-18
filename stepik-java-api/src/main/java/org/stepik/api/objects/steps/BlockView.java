package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class BlockView {
    private String name;
    private String text;
    private Video video;
    private Object animation;
    private BlockViewOptions options;
    @SerializedName("subtitle_files")
    private List<String> subtitleFiles;

    @NotNull
    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @NotNull
    public BlockViewOptions getOptions() {
        if (options == null) {
            options = new BlockViewOptions();
        }
        return options;
    }

    public void setOptions(@Nullable BlockViewOptions options) {
        this.options = options;
    }

    @NotNull
    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    @Nullable
    public Video getVideo() {
        return video;
    }

    public void setVideo(@Nullable Video video) {
        this.video = video;
    }

    @Nullable
    public Object getAnimation() {
        return animation;
    }

    public void setAnimation(@Nullable Object animation) {
        this.animation = animation;
    }

    @NotNull
    public List<String> getSubtitleFiles() {
        if (subtitleFiles == null) {
            subtitleFiles = new ArrayList<>();
        }
        return subtitleFiles;
    }

    public void setSubtitleFiles(@Nullable List<String> subtitleFiles) {
        this.subtitleFiles = subtitleFiles;
    }
}
