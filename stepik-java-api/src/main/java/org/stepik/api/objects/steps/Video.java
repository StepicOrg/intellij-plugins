package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Video {
    private String id;
    private String thumbnail;
    private List<VideoUrl> urls;
    private String status;
    @SerializedName("upload_date")
    private String uploadDate;
    private String filename;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
