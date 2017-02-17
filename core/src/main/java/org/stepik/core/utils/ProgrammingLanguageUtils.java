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
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.metrics.Metrics;

import java.io.IOException;
import java.util.ArrayList;

import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcPsiDirectory;

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

        if (currentLang.getMainFileName().equals(language.getMainFileName())) {
            targetStepNode.setCurrentLang(language);
            Metrics.switchLanguage(project, targetStepNode, SUCCESSFUL);
            return;
        }

        PsiDirectory src = getOrCreateSrcPsiDirectory(project, targetStepNode);
        if (src == null) {
            return;
        }

        PsiDirectory hide = ProjectFilesUtils.getOrCreatePsiDirectory(project, EduNames.HIDE);
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
        ArrayList<VirtualFile> needClose = closeStepNodeFile(project, targetStepNode);
        FileEditorManager.getInstance(project).openFile(second.getVirtualFile(), true);
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        needClose.forEach(editorManager::closeFile);

        Metrics.switchLanguage(project, targetStepNode, SUCCESSFUL);
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

    private static ArrayList<VirtualFile> closeStepNodeFile(
            @NotNull Project project,
            @NotNull StepNode targetStepNode) {
        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        ArrayList<VirtualFile> needClose = new ArrayList<>();
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            if (StudyUtils.getStudyNode(project, file) != targetStepNode) {
                continue;
            }
            Document document = documentManager.getDocument(file);
            if (document == null) {
                continue;
            }
            documentManager.saveDocument(document);
            needClose.add(file);
        }

        return needClose;
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
                            String template = stepNode.getTemplate(language);
                            file[0].setBinaryContent(template.getBytes());
                        } catch (IOException e) {
                            file[0] = null;
                        }
                    });
        }
        return PsiManager.getInstance(project).findFile(file[0]);
    }
}
