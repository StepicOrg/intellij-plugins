package com.jetbrains.tmp.learning.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StepType;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.queries.submissions.StepikSubmissionsPostQuery;
import org.stepik.plugin.actions.SendAction;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLInputElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FormListener implements EventListener {
    static final String EVENT_TYPE_SUBMIT = "submit";
    private static final Logger logger = Logger.getInstance(FormListener.class);
    private final Project project;

    FormListener(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void handleEvent(Event event) {
        String domEventType = event.getType();
        if (EVENT_TYPE_SUBMIT.equals(domEventType)) {
            HTMLFormElement form = (HTMLFormElement) event.getTarget();

            StudyNode root = StepikProjectManager.getProjectRoot(project);
            if (root == null) {
                return;
            }

            StudyNode node = StudyUtils.getStudyNode(root, form.getAction());
            if (node == null || !(node instanceof StepNode)) {
                return;
            }

            StepNode stepNode = (StepNode) node;

            HTMLCollection elements = form.getElements();

            String status = ((HTMLInputElement) elements.namedItem("status")).getValue();
            long attemptId = Long.parseLong(((HTMLInputElement) elements
                    .namedItem("attemptId")).getValue());

            String typeStr = ((HTMLInputElement) elements
                    .namedItem("type")).getValue();

            boolean locked = Boolean.valueOf(((HTMLInputElement) elements
                    .namedItem("locked")).getValue());

            StepType type = StepType.of(typeStr);

            try {
                switch (status) {
                    case "":
                    case "correct":
                    case "wrong":
                        if (!locked) {
                            getAttempt(stepNode);
                            StudyUtils.setStudyNode(project, node, true);
                        }
                        break;
                    case "active":
                        sendStep(stepNode, elements, type, attemptId);
                        break;
                    default:
                        return;
                }
            } catch (StepikClientException e) {
                logger.warn(e);
            }
            event.preventDefault();
        }
    }

    private void getAttempt(@NotNull StepNode node) {
        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

        stepikApiClient.attempts()
                .post()
                .step(node.getId())
                .execute();
    }

    private void sendStep(
            @NotNull StepNode stepNode,
            @NotNull HTMLCollection elements,
            @NotNull StepType type,
            long attemptId) {
        String title = "Checking Step: " + stepNode.getName();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

                    StepikSubmissionsPostQuery query = stepikApiClient.submissions()
                            .post()
                            .attempt(attemptId);
                    switch (type) {
                        case CHOICE:
                            List<Boolean> choices = getChoiceData(elements);
                            query.choices(choices);
                            break;
                        case STRING:
                            String text = getStringData(elements);
                            query.text(text);
                            break;
                        case SORTING:
                        case MATCHING:
                            List<Integer> ordering = getOrderingData(elements);
                            query.ordering(ordering);
                            break;
                    }

                    Submissions submissions = query.execute();

                    if (!submissions.isEmpty()) {
                        Submission submission = submissions.getSubmissions().get(0);
                        SendAction.checkStepStatus(project, stepNode, submission.getId(), indicator);
                    }
                } catch (StepikClientException e) {
                    logger.warn("Failed send step from browser", e);
                    StudyUtils.setStudyNode(project, stepNode, true);
                }
            }
        });
    }

    @NotNull
    private List<Boolean> getChoiceData(@NotNull HTMLCollection elements) {
        HTMLInputElement countElement = (HTMLInputElement) elements.namedItem("count");
        if (countElement == null) {
            return Collections.emptyList();
        }

        int count = Integer.parseInt(countElement.getValue());
        List<Boolean> choices = new ArrayList<>(count);
        for (int i = 0; i < elements.getLength(); i++) {
            HTMLInputElement option = ((HTMLInputElement) elements.item(i));
            if (option != null) {
                if ("option".equals(option.getName())) {
                    choices.add(option.getChecked());
                }
                if (!"hidden".equals(option.getType())) {
                    option.setDisabled(true);
                }
            }
        }
        return choices;
    }

    @NotNull
    private List<Integer> getOrderingData(@NotNull HTMLCollection elements) {
        HTMLInputElement countElement = (HTMLInputElement) elements.namedItem("count");
        if (countElement == null) {
            return Collections.emptyList();
        }

        int count = Integer.parseInt(countElement.getValue());
        List<Integer> ordering = new ArrayList<>(count);
        for (int i = 0; i < elements.getLength(); i++) {

            HTMLInputElement option = ((HTMLInputElement) elements.item(i));
            if (option != null) {
                if ("index".equals(option.getName())) {
                    String indexAttr = option.getValue();
                    ordering.add(Integer.valueOf(indexAttr));
                }
                if (!"hidden".equals(option.getType())) {
                    option.setDisabled(true);
                }
            }
        }
        return ordering;
    }

    private String getStringData(@NotNull HTMLCollection elements) {
        for (int i = 0; i < elements.getLength(); i++) {
            HTMLInputElement element = ((HTMLInputElement) elements.item(i));
            if (!"hidden".equals(element.getType())) {
                element.setDisabled(true);
            }
        }
        return ((HTMLInputElement) elements.namedItem("text")).getValue();
    }
}