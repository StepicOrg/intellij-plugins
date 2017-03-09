package com.jetbrains.tmp.learning.courseFormat.stepHelpers;

import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Video;
import org.stepik.api.objects.steps.VideoUrl;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * @author meanmail
 */
public class VideoStepNodeHelper {
    private final StepNode stepNode;
    private int quality;
    private List<VideoUrl> urls;

    public VideoStepNodeHelper(@NotNull StepNode stepNode) {
        this.stepNode = stepNode;
    }

    public boolean hasContent() {
        Step data = stepNode.getData();

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
        Step data = stepNode.getData();

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
        return urls.stream()
                .map(VideoUrl::getQuality)
                .collect(Collectors.toList());
    }

    public StepNode getStepNode() {
        return stepNode;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }
}
