package core.com.jetbrains.tmp.learning.actions;

import com.intellij.openapi.project.Project;
import core.com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import core.com.jetbrains.tmp.learning.courseFormat.Task;
import org.jetbrains.annotations.NotNull;

public abstract class StudyAfterCheckAction {
  public abstract void run(@NotNull final Project project, @NotNull final Task solvedTask, StudyStatus statusBeforeCheck);
}
