package org.stepik.api.objects.steps.queezes.code;

import org.stepik.api.objects.steps.queezes.BlockViewOptions;

import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class CodeOptions extends BlockViewOptions {
    private List<List<String>> samples;
    private Map<String, String> codeTemplates;
    private Map<String, Limit> limits;

    public Map<String, String> getCodeTemplates() {
        return codeTemplates;
    }

    public Map<String, Limit> getLimits() {
        return limits;
    }
}
