package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;

/**
 * @author meanmail
 */
public class BlockView {
    private String name;
    private String text;
    private String video;
    private String animation;
    private String options;
    @SerializedName("subtitle_files")
    private String[] subtitleFiles;
}
