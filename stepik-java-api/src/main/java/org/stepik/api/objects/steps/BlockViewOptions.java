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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockViewOptions that = (BlockViewOptions) o;

        if (samples != null ? !samples.equals(that.samples) : that.samples != null) return false;
        //noinspection SimplifiableIfStatement
        if (codeTemplates != null ? !codeTemplates.equals(that.codeTemplates) : that.codeTemplates != null)
            return false;
        return limits != null ? limits.equals(that.limits) : that.limits == null;
    }

    @Override
    public int hashCode() {
        int result = samples != null ? samples.hashCode() : 0;
        result = 31 * result + (codeTemplates != null ? codeTemplates.hashCode() : 0);
        result = 31 * result + (limits != null ? limits.hashCode() : 0);
        return result;
    }

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
