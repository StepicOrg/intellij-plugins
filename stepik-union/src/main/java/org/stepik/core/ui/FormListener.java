package org.stepik.core.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.submissions.Reply;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.queries.submissions.StepikSubmissionsPostQuery;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.StudyUtils;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StepType;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.stepik.StepikAuthManager;
import org.stepik.core.stepik.StepikAuthState;
import org.stepik.plugin.actions.SendAction;
import org.stepik.plugin.actions.navigation.StudyNavigator;
import org.stepik.plugin.utils.NavigationUtils;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.html.HTMLFormElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.ui.StepDescriptionUtils.getReply;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

class FormListener implements EventListener {
    static final String EVENT_TYPE_SUBMIT = "submit";
    private static final Logger logger = Logger.getInstance(FormListener.class);
    private final Project project;
    private final StudyBrowserWindow browser;

    FormListener(@NotNull Project project, @NotNull StudyBrowserWindow browser) {
        this.project = project;
        this.browser = browser;
    }

    @Nullable
    private static String getDataFromFile(@NotNull StepNode stepNode, @NotNull Project project) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        VirtualFile srcDirectory = getOrCreateSrcDirectory(project, stepNode, true);
        if (srcDirectory != null) {
            File initialDir = new File(srcDirectory.getPath());
            fileChooser.setInitialDirectory(initialDir);
        }
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                return lines.stream().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static void getAttempt(@NotNull Project project, @NotNull StepNode node) {
        StepikApiClient stepikApiClient = authAndGetStepikApiClient(true);

        stepikApiClient.attempts()
                .post()
                .step(node.getId())
                .executeAsync()
                .whenComplete((attempts, e) -> {
                    if (attempts != null) {
                        node.cleanLastReply();
                        StepikProjectManager.updateSelection(project);
                    } else {
                        logger.warn(e);
                    }
                });
    }

    private static void sendStep(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull Elements elements,
            @NotNull StepType type,
            long attemptId,
            @Nullable String data) {
        StepikApiClient stepikApiClient = authAndGetStepikApiClient(true);

        StepikSubmissionsPostQuery query = stepikApiClient.submissions()
                .post()
                .attempt(attemptId);
        Reply reply = getReply(stepNode, type, elements, data);
        if (reply == null) {
            return;
        }

        query.reply(reply)
                .executeAsync()
                .whenComplete(((submissions, e) -> {
                    if (submissions == null) {
                        logger.warn("Failed send step from browser", e);
                        StepikProjectManager.updateSelection(project);
                        return;
                    }

                    if (submissions.isEmpty()) {
                        logger.warn("Failed send step from browser", e);
                        return;
                    }

                    Submission submission = submissions.getFirst();
                    SendAction.checkStepStatus(project,
                            stepikApiClient,
                            stepNode,
                            submission.getId(),
                            new EmptyProgressIndicator());
                }));
    }

    static void handle(
            @NotNull Project project,
            @NotNull StudyBrowserWindow browser,
            @NotNull HTMLFormElement form) {
        StudyNode root = StepikProjectManager.getProjectRoot(project);
        if (root == null) {
            return;
        }

        StudyNode node = StudyUtils.getStudyNode(root, form.getAction());
        if (!(node instanceof StepNode)) {
            return;
        }

        StepNode stepNode = (StepNode) node;
        Elements elements = new Elements(form.getElements());

        switch (elements.getAction()) {
            case "get_first_attempt":
            case "get_attempt":
                if (!elements.isLocked()) {
                    getAttempt(project, stepNode);
                }
                break;
            case "submit":
                String typeStr = elements.getType();
                StepType type = StepType.of(typeStr);
                boolean isFromFile = elements.isFromFile();
                String data = isFromFile ? getDataFromFile(stepNode, project) : null;
                long attemptId = elements.getAttemptId();
                sendStep(project, stepNode, elements, type, attemptId, data);
                break;
            case "save_reply":
                typeStr = elements.getType();
                type = StepType.of(typeStr);
                getReply(stepNode, type, elements, null);
                break;
            case "login":
                String email = elements.getInputValue("email");
                String password = elements.getInputValue("password");
                browser.showLoadAnimation();
                StepikAuthManager.authentication(email, password)
                        .whenComplete((state, throwable) -> {
                            if (state != StepikAuthState.AUTH) {
                                browser.callFunction("setErrorMessage", "Wrong email or password");
                            }
                            browser.hideLoadAnimation();
                        });
                break;
            case "next_step":
                StudyNode targetNode = StudyNavigator.nextLeaf(stepNode);

                if (targetNode == null) {
                    return;
                }

                Platform.runLater(() -> NavigationUtils.navigate(project, targetNode));
                break;
            default:
                browser.hideLoadAnimation();
        }
    }

    @Override
    public void handleEvent(Event event) {
        String domEventType = event.getType();
        if (EVENT_TYPE_SUBMIT.equals(domEventType)) {
            HTMLFormElement form = (HTMLFormElement) event.getTarget();
            handle(project, browser, form);
            event.preventDefault();
            event.stopPropagation();
        }
    }
}