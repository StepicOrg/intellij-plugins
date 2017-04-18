package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Video;
import org.stepik.api.objects.steps.VideoUrl;
import org.stepik.core.courseFormat.StepNode;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * @author meanmail
 */
public class VideoTheoryHelper extends StepHelper {
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
            if (urls.get(i).getQuality() <= quality) {
                quality = urls.get(i).getQuality();
                return urls.get(i).getUrl();
            }
        }

        VideoUrl firstUrl = urls.get(0);
        quality = firstUrl.getQuality();
        return firstUrl.getUrl();
    }

    @NotNull
    private List<VideoUrl> getVideoUrls() {
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

    public Collection<? extends Integer> getQualitySet() {
        if (urls == null) {
            urls = getVideoUrls();
        }
        return urls.stream()
                .map(VideoUrl::getQuality)
                .collect(Collectors.toList());
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    @NotNull
    public String getType() {
        return "video";
    }

    @NotNull
    public String getLinkTitle() {
        return "Play this video in a browser";
    }
}
