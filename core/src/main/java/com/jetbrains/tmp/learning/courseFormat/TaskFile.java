package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of task file which contains task windows for student to type in and
 * which is visible to student in project view
 */

public class TaskFile {
    @Expose
    public String name;
    @Expose
    public String text;
    private boolean myUserCreated = false;
    private boolean myTrackChanges = true;
    private boolean myHighlightErrors = false;
    @Transient
    private Task myTask;

    public TaskFile() {
    }

    public void initTaskFile(final Task task) {
        setTask(task);
    }

    @Transient
    public Task getTask() {
        return myTask;
    }

    @Transient
    public void setTask(Task task) {
        myTask = task;
    }

    public static void copy(@NotNull final TaskFile source, @NotNull final TaskFile target) {
        target.name = source.name;
    }

    public void setUserCreated(boolean userCreated) {
        myUserCreated = userCreated;
    }

    public boolean isUserCreated() {
        return myUserCreated;
    }

    public boolean isTrackChanges() {
        return myTrackChanges;
    }

    public void setTrackChanges(boolean trackChanges) {
        myTrackChanges = trackChanges;
    }

    public boolean isHighlightErrors() {
        return myHighlightErrors;
    }

    public void setHighlightErrors(boolean highlightErrors) {
        myHighlightErrors = highlightErrors;
    }
}
