package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;

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

    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockViewOptions getOptions() {
        if (options == null) {
            options = new BlockViewOptions();
        }
        return options;
    }

    public void setOptions(BlockViewOptions options) {
        this.options = options;
    }

    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Object getAnimation() {
        return animation;
    }

    public void setAnimation(Object animation) {
        this.animation = animation;
    }

    public List<String> getSubtitleFiles() {
        if (subtitleFiles == null) {
            subtitleFiles = new ArrayList<>();
        }
        return subtitleFiles;
    }

    public void setSubtitleFiles(List<String> subtitleFiles) {
        this.subtitleFiles = subtitleFiles;
    }
}
