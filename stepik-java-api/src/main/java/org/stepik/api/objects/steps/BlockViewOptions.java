package org.stepik.api.objects.steps;

import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class BlockViewOptions {
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
