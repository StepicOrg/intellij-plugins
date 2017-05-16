package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.submissions.Choice;
import org.stepik.api.objects.submissions.Column;
import org.stepik.core.courseFormat.StepNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class TableQuizHelper extends QuizHelper {
    private Map<String, Map<String, Boolean>> choices;

    public TableQuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    @Override
    protected void done() {
        List<Choice> tableChoices = reply.getTableChoices();
        choices = new HashMap<>();

        if (tableChoices.isEmpty()) {
            List<String> rows = getDataset().getRows();
            List<String> cols = getDataset().getColumns();
            for (String row : rows) {
                Map<String, Boolean> map = new HashMap<>();
                for (String column : cols) {
                    map.put(column, false);
                }
                choices.put(row, map);
            }
            return;
        }

        for (Choice choice : tableChoices) {
            HashMap<String, Boolean> map = new HashMap<>();
            for (Column column : choice.getColumns()) {
                map.put(column.getName(), column.getAnswer());
            }
            choices.put(choice.getNameRow(), map);
        }
    }

    @Override
    void fail() {
        choices = new HashMap<>();
    }

    public boolean getChoice(@NotNull String rowName, @NotNull String colName) {
        initStepOptions();
        return choices.getOrDefault(rowName, new HashMap<>()).getOrDefault(colName, false);
    }

    @NotNull
    public String getDescription() {
        initStepOptions();
        return getDataset().getDescription();
    }

    @NotNull
    public List<String> getRows() {
        initStepOptions();
        return getDataset().getRows();
    }

    @NotNull
    public List<String> getColumns() {
        initStepOptions();
        return getDataset().getColumns();
    }

    public boolean isCheckbox() {
        initStepOptions();
        return getDataset().isCheckbox();
    }
}
