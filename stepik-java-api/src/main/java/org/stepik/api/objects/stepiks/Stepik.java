package org.stepik.api.objects.stepiks;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

/**
 * @author meanmail
 */
public class Stepik extends AbstractObject {
    private int profile;
    private Config config;
    private int user;
    @SerializedName("total_active")
    private int totalActive;
    @SerializedName("total_quizzes")
    private int totalQuizzes;

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    @NotNull
    public Config getConfig() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public void setConfig(@Nullable Config config) {
        this.config = config;
    }
}
