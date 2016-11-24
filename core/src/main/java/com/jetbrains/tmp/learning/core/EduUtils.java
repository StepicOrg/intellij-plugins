package com.jetbrains.tmp.learning.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Collection;

public class EduUtils {
    private EduUtils() {
    }

    private static final Logger logger = Logger.getInstance(EduUtils.class.getName());

    /**
     * Gets number index in directory names like "task1", "lesson2"
     *
     * @param fullName    full name of directory
     * @param logicalName part of name without index
     * @return index of object
     */
    public static int getIndex(@NotNull final String fullName, @NotNull final String logicalName) {
        if (!fullName.startsWith(logicalName)) {
            return -1;
        }
        try {
            return Integer.parseInt(fullName.substring(logicalName.length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean indexIsValid(int index, Collection collection) {
        int size = collection.size();
        return index >= 0 && index < size;
    }

    public static void createStudentFileFromAnswer(
            @NotNull final Project project,
            @NotNull final VirtualFile userFileDir,
            @NotNull final VirtualFile answerFileDir,
            @NotNull final String taskFileName, @NotNull final TaskFile taskFile) {
        VirtualFile file = userFileDir.findChild(taskFileName);
        if (file != null) {
            try {
                file.delete(project);
            } catch (IOException e) {
                logger.error(e);
            }
        }
        try {
            userFileDir.createChildData(project, taskFileName);
        } catch (IOException e) {
            logger.error(e);
        }

        file = userFileDir.findChild(taskFileName);
        if (file == null) {
            logger.info("Failed to find task file " + taskFileName);
            return;
        }
        VirtualFile answerFile = answerFileDir.findChild(taskFileName);
        if (answerFile == null) {
            return;
        }
        final Document answerDocument = FileDocumentManager.getInstance().getDocument(answerFile);
        if (answerDocument == null) {
            return;
        }
        final Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null) return;

        CommandProcessor.getInstance()
                .executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
                    document.replaceString(0, document.getTextLength(), answerDocument.getCharsSequence());
                    FileDocumentManager.getInstance().saveDocument(document);
                }), "Create Student File", "Create Student File");
        createStudentDocument(project, taskFile, document);
    }

    private static void createStudentDocument(
            @NotNull Project project,
            @NotNull TaskFile taskFile,
            final Document document) {
        EduDocumentListener listener = new EduDocumentListener(taskFile);
        document.addDocumentListener(listener);

        CommandProcessor.getInstance()
                .executeCommand(project,
                        () -> ApplicationManager.getApplication()
                                .runWriteAction(() -> FileDocumentManager.getInstance().saveDocument(document)),
                        "Create Student File",
                        "Create Student File");
        document.removeDocumentListener(listener);
    }

    public static boolean isImage(String fileName) {
        final String[] readerFormatNames = ImageIO.getReaderFormatNames();
        for (@NonNls String format : readerFormatNames) {
            final String ext = format.toLowerCase();
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
