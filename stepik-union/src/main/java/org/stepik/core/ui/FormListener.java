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
import org.stepik.api.objects.submissions.Choice;
import org.stepik.api.objects.submissions.Column;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.queries.submissions.StepikSubmissionsPostQuery;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.StudyUtils;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StepType;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.stepik.StepikConnectorLogin;
import org.stepik.plugin.actions.SendAction;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLSelectElement;
import org.w3c.dom.html.HTMLTextAreaElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.stepik.core.stepik.StepikConnectorLogin.authAndGetStepikApiClient;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

class FormListener implements EventListener {
    static final String EVENT_TYPE_SUBMIT = "submit";
    private static final Logger logger = Logger.getInstance(FormListener.class);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Project project;

    FormListener(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void handleEvent(Event event) {
        String domEventType = event.getType();
        if (EVENT_TYPE_SUBMIT.equals(domEventType)) {
            StudyNode root = StepikProjectManager.getProjectRoot(project);
            if (root == null) {
                return;
            }

            HTMLFormElement form = (HTMLFormElement) event.getTarget();

            StudyNode node = StudyUtils.getStudyNode(root, form.getAction());
            if (!(node instanceof StepNode)) {
                return;
            }

            StepNode stepNode = (StepNode) node;
            Elements elements = new Elements(form.getElements());

            try {
                switch (elements.getAction()) {
                    case "get_attempt":
                        boolean locked = elements.isLocked();
                        if (!locked) {
                            getAttempt(stepNode);
                            StepikProjectManager.updateSelection(project);
                        }
                        break;
                    case "submit":
                        String typeStr = elements.getType();
                        StepType type = StepType.of(typeStr);
                        boolean isFromFile = elements.isFromFile();
                        String data = isFromFile ? getDataFromFile(stepNode) : null;
                        long attemptId = elements.getAttemptId();

                        if (!isFromFile) {
                            sendStep(stepNode, elements, type, attemptId, null);
                        } else if (data != null) {
                            sendStep(stepNode, elements, type, attemptId, data);
                        }
                        break;
                    case "need_login":
                        executor.execute(() -> {
                            StepikConnectorLogin.authentication(true);
                            StepikProjectManager.updateSelection(project);
                        });
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

    @Nullable
    private String getDataFromFile(StepNode stepNode) {
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

    private void getAttempt(@NotNull StepNode node) {
        StepikApiClient stepikApiClient = authAndGetStepikApiClient(true);

        stepikApiClient.attempts()
                .post()
                .step(node.getId())
                .execute();
    }

    private void sendStep(
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
                    switch (type) {
                        case CHOICE:
                            List<Boolean> choices = getChoiceData(elements);
                            query.choices(choices);
                            break;
                        case STRING:
                            String text = getStringData(elements);
                            query.text(text);
                            break;
                        case FREE_ANSWER:
                            text = getStringData(elements);
                            query.text(text);
                            query.attachments(emptyList());
                            break;
                        case NUMBER:
                            String number = getStringData(elements);
                            query.number(number);
                            break;
                        case DATASET:
                            String dataset;
                            if (data == null) {
                                dataset = getStringData(elements);
                            } else {
                                dataset = data;
                            }
                            query.file(dataset);
                            break;
                        case TABLE:
                            List tableChoices = getChoices(elements);
                            query.choices(tableChoices);
                            break;
                        case FILL_BLANKS:
                            List<String> blanks = getBlanks(elements);
                            query.blanks(blanks);
                            break;
                        case SORTING:
                        case MATCHING:
                            List<Integer> ordering = getOrderingData(elements);
                            query.ordering(ordering);
                            break;
                        case MATH:
                            String formula = getStringData(elements);
                            query.formula(formula);
                            break;
                        default:
                            logger.warn("Unknown step type tried sending: " + type);
                            return;
                    }

                    Submissions submissions = query.execute();

                    if (!submissions.isEmpty()) {
                        Submission submission = submissions.getFirst();
                        SendAction.checkStepStatus(project, stepNode, submission.getId(), indicator);
                    }
                } catch (StepikClientException e) {
                    logger.warn("Failed send step from browser", e);
                    StepikProjectManager.updateSelection(project);
                }
            }
        });
    }

    private void forEachInputElement(@NotNull Elements elements, Consumer<HTMLInputElement> consumer) {
        for (Node node : elements) {
            if (node instanceof HTMLInputElement) {
                HTMLInputElement element = (HTMLInputElement) node;
                consumer.accept(element);
                element.setDisabled(true);
            }
        }
    }

    private void disableAllInputs(@NotNull Elements elements) {
        for (Node node : elements) {
            if (node instanceof HTMLInputElement) {
                HTMLInputElement element = (HTMLInputElement) node;
                element.setDisabled(true);
            } else if (node instanceof HTMLTextAreaElement) {
                HTMLTextAreaElement element = (HTMLTextAreaElement) node;
                element.setDisabled(true);
            }
        }
    }

    @NotNull
    private List<Boolean> getChoiceData(@NotNull Elements elements) {
        List<Boolean> choices = new ArrayList<>();

        forEachInputElement(elements, element -> {
            if ("option".equals(element.getName())) {
                choices.add(element.getChecked());
            }
        });

        return choices;
    }

    @NotNull
    private List<Integer> getOrderingData(@NotNull Elements elements) {
        List<Integer> ordering = new ArrayList<>();

        forEachInputElement(elements, element -> {
            if ("index".equals(element.getName())) {
                String indexAttr = element.getValue();
                ordering.add(Integer.valueOf(indexAttr));
            }
        });
        return ordering;
    }

    private String getStringData(@NotNull Elements elements) {
        disableAllInputs(elements);
        return elements.getInputValue("text");
    }

    @NotNull
    private List<Choice> getChoices(@NotNull Elements elements) {
        Map<String, List<Column>> choices = new HashMap<>();
        List<String> rows = new ArrayList<>();

        forEachInputElement(elements, element -> {
            String type = element.getType();

            if ("checkbox".equals(type) || "radio".equals(type)) {
                List<Column> columns = choices.computeIfAbsent(element.getName(), k -> {
                    rows.add(k);
                    return new ArrayList<>();
                });
                Column column = new Column(element.getValue(), element.getChecked());
                columns.add(column);
            }
        });

        return choices.entrySet().stream()
                .map(entry -> new Choice(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(choice -> rows.indexOf(choice.getNameRow())))
                .collect(Collectors.toList());
    }

    private List<String> getBlanks(@NotNull Elements elements) {
        List<String> blanks = new ArrayList<>();

        for (Node node : elements) {
            if (node instanceof HTMLInputElement) {
                HTMLInputElement element = (HTMLInputElement) node;
                String type = element.getType();
                if ("text".equals(type)) {
                    blanks.add(element.getValue());
                }
                element.setDisabled(true);
            } else if (node instanceof HTMLSelectElement) {
                HTMLSelectElement element = (HTMLSelectElement) node;
                blanks.add(element.getValue());
                element.setDisabled(true);
            }
        }

        return blanks;
    }

    class Elements implements Iterable<Node> {
        private final HTMLCollection elements;

        Elements(@NotNull HTMLCollection elements) {
            this.elements = elements;
        }

        @NotNull
        String getAction() {
            Node item = elements.namedItem("action");
            if (item instanceof HTMLInputElement) {
                return ((HTMLInputElement) item).getValue();
            }

            return "";
        }

        @NotNull
        String getType() {
            Node item = elements.namedItem("type");
            if (item instanceof HTMLInputElement) {
                return ((HTMLInputElement) item).getValue();
            }

            return "";
        }

        @NotNull
        String getInputValue(@NotNull String name) {
            Node item = elements.namedItem(name);
            String value = null;
            if (item instanceof HTMLInputElement) {
                value = ((HTMLInputElement) item).getValue();
            } else if (item instanceof HTMLTextAreaElement) {
                value = ((HTMLTextAreaElement) item).getValue();
            }
            return value != null ? value : "";
        }

        boolean isFromFile() {
            HTMLInputElement element = ((HTMLInputElement) elements.namedItem("isFromFile"));
            return element != null && "true".equals(element.getValue());
        }

        long getAttemptId() {
            String value = getInputValue("attemptId");
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }

        Boolean isLocked() {
            return Boolean.valueOf(getInputValue("locked"));
        }

        @NotNull
        @Override
        public Iterator<Node> iterator() {
            return new Iterator<Node>() {
                private final int size = elements.getLength();
                private int index;

                @Override
                public boolean hasNext() {
                    return index < size;
                }

                @NotNull
                @Override
                public Node next() {
                    return elements.item(index++);
                }
            };
        }
    }
}