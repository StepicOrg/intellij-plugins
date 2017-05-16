package org.stepik.core.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.submissions.Reply;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.queries.submissions.StepikSubmissionsPostQuery;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.StudyUtils;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StepType;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.stepik.StepikAuthManager;
import org.stepik.core.stepik.StepikAuthState;
import org.stepik.plugin.actions.SendAction;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.html.HTMLFormElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.ui.StepDescriptionUtils.getReply;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

class FormListener implements EventListener {
    static final String EVENT_TYPE_SUBMIT = "submit";
    private static final Logger logger = Logger.getInstance(FormListener.class);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
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

    private static void getAttempt(@NotNull StepNode node) {
        StepikApiClient stepikApiClient = authAndGetStepikApiClient(true);

        stepikApiClient.attempts()
                .post()
                .step(node.getId())
                .execute();
    }

    private static void sendStep(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull Elements elements,
            @NotNull StepType type,
            long attemptId,
            @Nullable String data) {
        String title = "Checking Step: " + stepNode.getName();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    StepikApiClient stepikApiClient = authAndGetStepikApiClient(true);

                    StepikSubmissionsPostQuery query = stepikApiClient.submissions()
                            .post()
                            .attempt(attemptId);
                    Reply reply = getReply(stepNode, type, elements, data);
                    if (reply == null) {
                        return;
                    }
                    Submissions submissions = query.reply(reply).execute();

                    if (!submissions.isEmpty()) {
                        Submission submission = submissions.getFirst();
                        SendAction.checkStepStatus(project, stepikApiClient, stepNode, submission.getId(), indicator);
                    }
                } catch (StepikClientException e) {
                    logger.warn("Failed send step from browser", e);
                    StepikProjectManager.updateSelection(project);
                }
            }
        });
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

        try {
            switch (elements.getAction()) {
                case "get_first_attempt":
                case "get_attempt":
                    boolean locked = elements.isLocked();
                    if (!locked) {
                        getAttempt(stepNode);
                        stepNode.cleanLastReply();
                        StepikProjectManager.updateSelection(project);
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
                case "need_login":
                    executor.execute(() -> StepikAuthManager.authentication(true));
                    break;
                case "save_reply":
                    typeStr = elements.getType();
                    type = StepType.of(typeStr);
                    getReply(stepNode, type, elements, null);
                    break;
                case "login":
                    executor.execute(() -> {
                        browser.showLoadAnimation();
                        String email = elements.getInputValue("login-form-email");
                        String password = elements.getInputValue("login-form-password");
                        StepikAuthState state = StepikAuthManager.authentication(email, password);
                        if (state != StepikAuthState.AUTH) {
                            browser.callFunction("setErrorMessage", "Wrong email or password");
                        }
                        browser.hideLoadAnimation();
                    });
                    break;
                default:
                    browser.hideLoadAnimation();
            }
        } catch (StepikClientException e) {
            logger.warn(e);
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