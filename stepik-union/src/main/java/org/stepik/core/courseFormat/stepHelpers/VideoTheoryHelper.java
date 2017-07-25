package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Video;
import org.stepik.api.objects.steps.VideoUrl;
import org.stepik.core.courseFormat.StepNode;

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.stepik.core.utils.PluginUtils.PLUGIN_ID;

/**
 * @author meanmail
 */
public class VideoTheoryHelper extends StepHelper {
    public static final String VIDEO_QUALITY_PROPERTY_NAME = PLUGIN_ID + ".VIDEO_QUALITY";
    private int quality;
    private List<VideoUrl> urls;

    public VideoTheoryHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    public boolean hasContent() {
        Step data = getStepNode().getData();

        if (data == null) {
            return false;
        }
        Video video = data.getBlock().getVideo();
        List<VideoUrl> urls = video.getUrls();
        return !urls.isEmpty();
    }

    @Nullable
    public String getUrl() {
        if (urls == null) {
            urls = getVideoUrls();
        }

        if (urls.isEmpty()) {
            return null;
        }

        for (int i = urls.size() - 1; i > 0; i--) {
            if (urls.get(i).getQuality() <= getQuality()) {
                setQuality(urls.get(i).getQuality());
                return urls.get(i).getUrl();
            }
        }

        VideoUrl firstUrl = urls.get(0);
        setQuality(firstUrl.getQuality());
        return firstUrl.getUrl();
    }

    @NotNull
    public List<VideoUrl> getVideoUrls() {
        Step data = getStepNode().getData();

        if (data == null) {
            return emptyList();
        }
        Video video = data.getBlock().getVideo();
        List<VideoUrl> urls = video.getUrls();
        if (urls.isEmpty()) {
            return emptyList();
        }

        urls.sort(Comparator.comparingInt(VideoUrl::getQuality));
        return urls;
    }

    public int getQuality() {
        if (quality == 0) {
            quality = Integer.parseInt(PropertiesComponent.getInstance()
                    .getValue(VIDEO_QUALITY_PROPERTY_NAME, String.valueOf(0)));
        }

        return quality;
    }

    private void setQuality(int quality) {
        this.quality = quality;
        PropertiesComponent.getInstance().setValue(VIDEO_QUALITY_PROPERTY_NAME, String.valueOf(quality));
    }

    @NotNull
    public String getType() {
        return "video";
    }
}
