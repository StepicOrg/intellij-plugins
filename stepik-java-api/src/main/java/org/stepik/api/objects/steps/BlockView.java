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

    @NotNull
    public Video getVideo() {
        if (video == null) {
            video = new Video();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockView blockView = (BlockView) o;

        if (name != null ? !name.equals(blockView.name) : blockView.name != null) return false;
        if (text != null ? !text.equals(blockView.text) : blockView.text != null) return false;
        if (video != null ? !video.equals(blockView.video) : blockView.video != null) return false;
        if (animation != null ? !animation.equals(blockView.animation) : blockView.animation != null) return false;
        //noinspection SimplifiableIfStatement
        if (options != null ? !options.equals(blockView.options) : blockView.options != null) return false;
        return subtitleFiles != null ? subtitleFiles.equals(blockView.subtitleFiles) : blockView.subtitleFiles == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (video != null ? video.hashCode() : 0);
        result = 31 * result + (animation != null ? animation.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (subtitleFiles != null ? subtitleFiles.hashCode() : 0);
        return result;
    }
}
