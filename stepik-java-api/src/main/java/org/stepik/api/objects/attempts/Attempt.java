package org.stepik.api.objects.attempts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class Attempt {
    private int id;
    private String dataset;
    @SerializedName("dataset_url")
    private String datasetUrl;
    private String time;
    private String status;
    @SerializedName("time_left")
    private int timeLeft;
    private int step;
    private int user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getDataset() {
        return dataset;
    }

    public void setDataset(@Nullable String dataset) {
        this.dataset = dataset;
    }

    @Nullable
    public String getDatasetUrl() {
        return datasetUrl;
    }

    public void setDatasetUrl(@Nullable String datasetUrl) {
        this.datasetUrl = datasetUrl;
    }

    @Nullable
    public String getTime() {
        return time;
    }

    public void setTime(@Nullable String time) {
        this.time = time;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
