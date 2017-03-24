package org.stepik.api.objects.attempts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

/**
 * @author meanmail
 */
public class Attempt extends AbstractObject {
    private Dataset dataset;
    @SerializedName("dataset_url")
    private String datasetUrl;
    private String time;
    private String status;
    @SerializedName("time_left")
    private int timeLeft;
    private int step;
    private int user;

    @NotNull
    public Dataset getDataset() {
        if (dataset == null) {
            dataset = new Dataset();
        }
        return dataset;
    }

    public void setDataset(@Nullable Dataset dataset) {
        this.dataset = dataset;
    }

    @NotNull
    public String getDatasetUrl() {
        if (datasetUrl == null) {
            datasetUrl = "";
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Attempt attempt = (Attempt) o;

        if (timeLeft != attempt.timeLeft) return false;
        if (step != attempt.step) return false;
        if (user != attempt.user) return false;
        if (dataset != null ? !dataset.equals(attempt.dataset) : attempt.dataset != null) return false;
        if (datasetUrl != null ? !datasetUrl.equals(attempt.datasetUrl) : attempt.datasetUrl != null) return false;
        //noinspection SimplifiableIfStatement
        if (time != null ? !time.equals(attempt.time) : attempt.time != null) return false;
        return status != null ? status.equals(attempt.status) : attempt.status == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dataset != null ? dataset.hashCode() : 0);
        result = 31 * result + (datasetUrl != null ? datasetUrl.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + timeLeft;
        result = 31 * result + step;
        result = 31 * result + user;
        return result;
    }
}
