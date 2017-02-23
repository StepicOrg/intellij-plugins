package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObjectWithStringId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Video extends AbstractObjectWithStringId {
    private String thumbnail;
    private List<VideoUrl> urls;
    private String status;
    @SerializedName("upload_date")
    private String uploadDate;
    private String filename;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Video video = (Video) o;

        if (thumbnail != null ? !thumbnail.equals(video.thumbnail) : video.thumbnail != null) return false;
        if (urls != null ? !urls.equals(video.urls) : video.urls != null) return false;
        if (status != null ? !status.equals(video.status) : video.status != null) return false;
        //noinspection SimplifiableIfStatement
        if (uploadDate != null ? !uploadDate.equals(video.uploadDate) : video.uploadDate != null) return false;
        return filename != null ? filename.equals(video.filename) : video.filename == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + (urls != null ? urls.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (uploadDate != null ? uploadDate.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        return result;
    }

    @Nullable
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(@Nullable String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @NotNull
    public List<VideoUrl> getUrls() {
        if (urls == null) {
            urls = new ArrayList<>();
        }
        return urls;
    }

    public void setUrls(@Nullable List<VideoUrl> urls) {
        this.urls = urls;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    @Nullable
    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(@Nullable String uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Nullable
    public String getFilename() {
        return filename;
    }

    public void setFilename(@Nullable String filename) {
        this.filename = filename;
    }
}
