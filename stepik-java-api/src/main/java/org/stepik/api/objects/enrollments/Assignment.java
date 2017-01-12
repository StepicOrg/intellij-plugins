package org.stepik.api.objects.enrollments;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

/**
 * @author meanmail
 */
public class Assignment extends AbstractObject {
    private int unit;
    private int step;
    private String progress;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("update_date")
    private String updateDate;

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Nullable
    public String getProgress() {
        return progress;
    }

    public void setProgress(@Nullable String progress) {
        this.progress = progress;
    }

    @Nullable
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(@Nullable String createDate) {
        this.createDate = createDate;
    }

    @Nullable
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(@Nullable String updateDate) {
        this.updateDate = updateDate;
    }
}
