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
    @Transient
    private Task myTask;

    public TaskFile() {
    }

    public void initTaskFile(final Task task) {
        myTask = task;
    }

    @Transient
    public Task getTask() {
        return myTask;
    }

    public static void copy(@NotNull final TaskFile source, @NotNull final TaskFile target) {
        target.name = source.name;
    }
}
