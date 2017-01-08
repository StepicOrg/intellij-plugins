package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;

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

    public Map<String, String> getCodeTemplates() {
        if (codeTemplates == null) {
            codeTemplates = new HashMap<>();
        }
        return codeTemplates;
    }

    public void setCodeTemplates(Map<String, String> codeTemplates) {
        this.codeTemplates = codeTemplates;
    }

    public Map<String, Limit> getLimits() {
        if (limits == null) {
            limits = new HashMap<>();
        }
        return limits;
    }

    public void setLimits(Map<String, Limit> limits) {
        this.limits = limits;
    }

    public List<Sample> getSamples() {
        if (samples == null) {
            samples = new ArrayList<>();
        }
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }
}
