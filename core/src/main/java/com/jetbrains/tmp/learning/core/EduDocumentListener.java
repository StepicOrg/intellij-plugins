package com.jetbrains.tmp.learning.core;

import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;

/**
 * Listens changes in study files and updates
 * coordinates of all the windows in current task file
 */
public class EduDocumentListener extends DocumentAdapter {
    private final TaskFile myTaskFile;

    public EduDocumentListener(TaskFile taskFile) {
        myTaskFile = taskFile;
    }

    //remembering old end before document change because of problems
    // with fragments containing "\n"
    @Override
    public void beforeDocumentChange(DocumentEvent e) {
        if (!myTaskFile.isTrackChanges()) {
            return;
        }
        myTaskFile.setHighlightErrors(true);
    }
}