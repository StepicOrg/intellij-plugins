package org.stepik.api.objects.steps;

import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class VideoUrl {
    private String url;
    private String quality;

    @Nullable
    public String getUrl() {
        return url;
    }

    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    @Nullable
    public String getQuality() {
        return quality;
    }

    public void setQuality(@Nullable String quality) {
        this.quality = quality;
    }
}
