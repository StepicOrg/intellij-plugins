package org.stepik.core.utils;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.objects.users.User;
import org.stepik.api.queries.Order;
import org.stepik.core.StudyUtils;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.core.EduNames;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.metrics.Metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.getCurrentUser;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreatePsiDirectory;
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

        language.getTestRunner().updateRunConfiguration(project, targetStepNode);

        SupportedLanguages currentLang = targetStepNode.getCurrentLang();
        String currentMainFileName = currentLang.getMainFileName();

        String mainFilePath = String.join("/", targetStepNode.getPath(), EduNames.SRC, currentMainFileName);
        VirtualFile mainFile = project.getBaseDir().findFileByRelativePath(mainFilePath);

        boolean mainFileExists = mainFile != null;

        if (currentLang == language && mainFileExists) {
            return;
        }

        if (currentMainFileName.equals(language.getMainFileName()) && mainFileExists) {
            targetStepNode.setCurrentLang(language);
            Metrics.switchLanguage(project, targetStepNode, SUCCESSFUL);
            return;
        }

        PsiDirectory src = getOrCreateSrcPsiDirectory(project, targetStepNode);
        if (src == null) {
            return;
        }

        PsiDirectory hide = getOrCreatePsiDirectory(project, src, EduNames.HIDE);
        if (hide == null) {
            return;
        }

        PsiFile second = findFile(src, language.getMainFileName());
        boolean moveSecond = second == null;
        if (moveSecond) {
            second = getOrCreateMainFile(project, hide.getVirtualFile(), language, targetStepNode);
            if (second == null) {
                logger.error("Can't create Main file: " + language.getMainFileName());
                return;
            }
        }

        PsiFile first = findFile(hide, currentMainFileName);
        boolean moveFirst = first == null;

        if (moveFirst) {
            first = findFile(src, currentMainFileName);
            moveFirst = !second.isEquivalentTo(first);
        }

        targetStepNode.setCurrentLang(language);
        ArrayList<VirtualFile> needClose = closeStepNodeFile(project, targetStepNode);

        PsiFile finalSecond = second;
        PsiFile finalFirst = first;
        boolean finalMoveFirst = moveFirst;
        ApplicationManager.getApplication()
                .invokeAndWait(() -> {
                    FileEditorManager.getInstance(project).openFile(finalSecond.getVirtualFile(), true);
                    FileEditorManager editorManager = FileEditorManager.getInstance(project);
                    needClose.forEach(editorManager::closeFile);

                    exchangeFiles(src, hide, finalFirst, finalSecond, finalMoveFirst, moveSecond);

                    ProjectView.getInstance(project).selectPsiElement(finalSecond, false);
                });

        Metrics.switchLanguage(project, targetStepNode, SUCCESSFUL);
    }

    @Nullable
    private static PsiFile findFile(@NotNull PsiDirectory parent, @NotNull String name) {
        return ApplicationManager.getApplication()
                .runReadAction((Computable<PsiFile>) () ->
                        parent.findFile(name)
                );
    }

    private static void exchangeFiles(
            @NotNull PsiDirectory src,
            @NotNull PsiDirectory hide,
            @Nullable PsiFile first,
            @NotNull PsiFile second,
            boolean moveFirst,
            boolean moveSecond) {
        if (!moveFirst && !moveSecond) {
            return;
        }

        ApplicationManager.getApplication().runWriteAction(() -> {
            if (moveFirst && first != null) {
                MoveFilesOrDirectoriesUtil.doMoveFile(first, hide);
            }

            if (moveSecond) {
                MoveFilesOrDirectoriesUtil.doMoveFile(second, src);
            }
        });
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
            final Document document = ApplicationManager.getApplication()
                    .runReadAction((Computable<Document>) () ->
                            documentManager.getDocument(file)
                    );
            if (document == null) {
                continue;
            }
            ApplicationManager.getApplication().invokeAndWait(() ->
                    documentManager.saveDocument(document)
            );
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

        Application application = ApplicationManager.getApplication();
        if (file[0] == null) {
            application.invokeAndWait(() ->
                    application.runWriteAction(() -> {
                        try {
                            file[0] = parent.createChildData(null, fileName);
                            String template = null;

                            StepikApiClient stepikApiClient = authAndGetStepikApiClient();
                            User user = getCurrentUser();
                            if (!user.isGuest()) {
                                try {
                                    Submissions submissions = stepikApiClient.submissions()
                                            .get()
                                            .user(user.getId())
                                            .order(Order.DESC)
                                            .step(stepNode.getId())
                                            .execute();

                                    if (!submissions.isEmpty()) {
                                        Optional<Submission> lastSubmission = submissions.getItems()
                                                .stream()
                                                .filter(submission -> SupportedLanguages.Companion.langOfName(submission
                                                        .getReply()
                                                        .getLanguage()) == language)
                                                .limit(1)
                                                .findFirst();
                                        if (lastSubmission.isPresent()) {
                                            template = lastSubmission.get().getReply().getCode();
                                        }
                                    }
                                } catch (StepikClientException e) {
                                    logger.warn(e);
                                }
                            }

                            if (template == null) {
                                template = stepNode.getTemplate(language);
                            }

                            file[0].setBinaryContent(template.getBytes());
                        } catch (IOException e) {
                            file[0] = null;
                        }
                    })
            );
        }
        return application.runReadAction((Computable<PsiFile>) () ->
                PsiManager.getInstance(project).findFile(file[0])
        );
    }
}
