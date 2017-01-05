package org.stepik.api.objects.stepiks;

import com.google.gson.annotations.SerializedName;

/**
 * @author meanmail
 */
public class Stepik {
    private int profile;
    private Config config;
    private int user;
    @SerializedName("total_active")
    private int totalActive;
    @SerializedName("total_quizzes")
    private int totalQuizzes;
    private int id;
}
