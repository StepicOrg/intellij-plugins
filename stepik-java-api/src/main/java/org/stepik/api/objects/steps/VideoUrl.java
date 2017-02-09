package org.stepik.api.objects.steps;

import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class VideoUrl {
    private String url;
    private String quality;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoUrl videoUrl = (VideoUrl) o;

        //noinspection SimplifiableIfStatement
        if (url != null ? !url.equals(videoUrl.url) : videoUrl.url != null) return false;
        return quality != null ? quality.equals(videoUrl.quality) : videoUrl.quality == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (quality != null ? quality.hashCode() : 0);
        return result;
    }

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
