package com.jetbrains.tmp.learning.editor;


import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.problems.WolfTheProblemSolver;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.core.EduDocumentListener;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import com.jetbrains.tmp.learning.ui.StudyToolWindowFactory;
import org.jetbrains.annotations.NotNull;


public class StudyEditorFactoryListener implements EditorFactoryListener {
    @Override
    public void editorCreated(@NotNull final EditorFactoryEvent event) {
        final Editor editor = event.getEditor();
        final Project project = editor.getProject();
        if (project == null) {
            return;
        }

        final Document document = editor.getDocument();
        final VirtualFile openedFile = FileDocumentManager.getInstance().getFile(document);
        if (openedFile != null) {
            final TaskFile taskFile = StudyUtils.getTaskFile(project, openedFile);
            if (taskFile != null) {
                WolfTheProblemSolver.getInstance(project).clearProblems(openedFile);
                final ToolWindow studyToolWindow = ToolWindowManager.getInstance(project)
                        .getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
                if (studyToolWindow != null) {
                    StudyUtils.updateToolWindows(project);
                    studyToolWindow.show(null);
                }
                Course course = StudyTaskManager.getInstance(project).getCourse();
                if (course == null) {
                    return;
                }

                StudyEditor.addDocumentListener(document, new EduDocumentListener(taskFile));
            }
        }
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        final Editor editor = event.getEditor();
        final Document document = editor.getDocument();
        StudyEditor.removeListener(document);
        editor.getMarkupModel().removeAllHighlighters();
        editor.getSelectionModel().removeSelection();
    }
}
