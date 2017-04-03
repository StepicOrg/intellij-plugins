package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.steps.Sample;
import org.stepik.core.courseFormat.StepNode;

import java.util.List;

/**
 * @author meanmail
 */
public class CodeHelper extends StepHelper {
    public CodeHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    @NotNull
    public String getLinkTitle() {
        return "View this step on Stepik";
    }

    public int getTimeLimit() {
        return getStepNode().getLimit().getTime();
    }

    public int getMemoryLimit() {
        return getStepNode().getLimit().getMemory();
    }

    public String getSamples() {
        StringBuilder stringBuilder = new StringBuilder();

        List<Sample> samples = getStepNode().getSamples();

        for (int i = 1; i <= samples.size(); i++) {
            Sample sample = samples.get(i - 1);
            stringBuilder.append("<p><b>Sample Input ")
                    .append(i)
                    .append(":</b><br>")
                    .append(sample.getInput())
                    .append("<br>")
                    .append("<b>Sample Output ")
                    .append(i)
                    .append(":</b><br>")
                    .append(sample.getOutput())
                    .append("<br>");
        }

        return stringBuilder.toString().replaceAll("\\n", "<br>");
    }
}
