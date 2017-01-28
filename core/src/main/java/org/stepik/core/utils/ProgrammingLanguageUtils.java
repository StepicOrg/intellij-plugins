package org.stepik.core.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesUtil;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ProgrammingLanguageUtils {
    private static final Logger logger = Logger.getInstance(ProgrammingLanguageUtils.class);

    public static void switchProgrammingLanguage(
            @NotNull Project project,
            @NotNull StepNode targetStepNode,
            @NotNull SupportedLanguages language) {
        if (!targetStepNode.getSupportedLanguages().contains(language)) {
            return;
        }

        SupportedLanguages currentLang = targetStepNode.getCurrentLang();

        if (currentLang == language) {
            return;
        }

        closeStepNodeFile(project, targetStepNode);

        VirtualFile srcParent = project.getBaseDir().findFileByRelativePath(targetStepNode.getPath());
        if (srcParent == null) {
            return;
        }

        PsiDirectory src = getOrCreateDir(project, srcParent, EduNames.SRC);
        if (src == null) {
            return;
        }

        PsiDirectory hide = getOrCreateDir(project, src.getVirtualFile(), EduNames.HIDE);
        if (hide == null) {
            return;
        }

        PsiFile second = src.findFile(language.getMainFileName());
        boolean moveSecond = second == null;
        if (moveSecond) {
            second = getOrCreateMainFile(project, hide.getVirtualFile(), language, targetStepNode);
            if (second == null) {
                logger.error("Can't create Main file: " + language.getMainFileName());
                return;
            }
        }

        PsiFile first = hide.findFile(currentLang.getMainFileName());
        boolean moveFirst = first == null;

        if (moveFirst) {
            first = src.findFile(currentLang.getMainFileName());
        }

        exchangeFiles(src, hide, first, second, moveFirst, moveSecond);

        targetStepNode.setCurrentLang(language);
        FileEditorManager.getInstance(project).openFile(second.getVirtualFile(), true);
    }

    private static void exchangeFiles(
            @NotNull PsiDirectory src,
            @NotNull PsiDirectory hide,
            @Nullable PsiFile first,
            @NotNull PsiFile second,
            boolean moveFirst,
            boolean moveSecond) {

        if (moveFirst || moveSecond) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                if (moveFirst && first != null) {
                    MoveFilesOrDirectoriesUtil.doMoveFile(first, hide);
                }

                if (moveSecond) {
                    MoveFilesOrDirectoriesUtil.doMoveFile(second, src);
                }
            });
        }
    }

    private static void closeStepNodeFile(@NotNull Project project, @NotNull StepNode targetStepNode) {
        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            StudyUtils.getStep(project, file);
            if (StudyUtils.getStep(project, file) != targetStepNode) {
                continue;
            }
            Document document = documentManager.getDocument(file);
            if (document == null) {
                continue;
            }
            documentManager.saveDocument(document);
            editorManager.closeFile(file);
        }
    }

    private static PsiDirectory getOrCreateDir(
            @NotNull Project project,
            @NotNull VirtualFile parent,
            @NotNull String name) {
        final VirtualFile[] hideVF = {parent.findChild(name)};

        if (hideVF[0] == null) {
            ApplicationManager
                    .getApplication()
                    .runWriteAction(() -> {
                        try {
                            hideVF[0] = parent.createChildDirectory(null, name);
                        } catch (IOException e) {
                            hideVF[0] = null;
                        }
                    });
            if (hideVF[0] == null) {
                return null;
            }
        }

        return PsiManager.getInstance(project).findDirectory(hideVF[0]);
    }

    @Nullable
    private static PsiFile getOrCreateMainFile(
            @NotNull Project project,
            @NotNull VirtualFile parent,
            @NotNull SupportedLanguages language,
            @NotNull StepNode stepNode) {
        String fileName = language.getMainFileName();
        final VirtualFile[] file = {parent.findChild(fileName)};

        if (file[0] == null) {
            ApplicationManager
                    .getApplication()
                    .runWriteAction(() -> {
                        try {
                            file[0] = parent.createChildData(null, fileName);

                            StepFile stepFile = stepNode.getStepFiles().get(fileName);
                            if (stepFile != null) {
                                file[0].setBinaryContent(stepFile.getText().getBytes());
                            }
                        } catch (IOException e) {
                            file[0] = null;
                        }
                    });
        }
        return PsiManager.getInstance(project).findFile(file[0]);
    }
}
