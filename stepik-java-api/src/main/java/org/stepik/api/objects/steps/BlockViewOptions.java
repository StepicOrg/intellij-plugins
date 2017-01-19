package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class BlockViewOptions {
    private List<Sample> samples;
    @SerializedName("code_templates")
    private Map<String, String> codeTemplates;
    private Map<String, Limit> limits;

    @NotNull
    public Map<String, String> getCodeTemplates() {
        if (codeTemplates == null) {
            codeTemplates = new HashMap<>();
        }
        return codeTemplates;
    }

    public void setCodeTemplates(@Nullable Map<String, String> codeTemplates) {
        this.codeTemplates = codeTemplates;
    }

    @NotNull
    public Map<String, Limit> getLimits() {
        if (limits == null) {
            limits = new HashMap<>();
        }
        return limits;
    }

    public void setLimits(@Nullable Map<String, Limit> limits) {
        this.limits = limits;
    }

    @NotNull
    public List<Sample> getSamples() {
        if (samples == null) {
            samples = new ArrayList<>();
        }
        return samples;
    }

    public void setSamples(@Nullable List<Sample> samples) {
        this.samples = samples;
    }
}
