package org.stepik.core.ui;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.submissions.Choice;
import org.stepik.api.objects.submissions.Column;
import org.stepik.api.objects.submissions.Reply;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StepType;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLSelectElement;
import org.w3c.dom.html.HTMLTextAreaElement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

class StepDescriptionUtils {
    private static final Logger logger = Logger.getInstance(StepDescriptionUtils.class);

    @Nullable
    static Reply getReply(
            @NotNull StepNode stepNode, @NotNull StepType type,
            @NotNull Elements elements,
            @Nullable String data) {
        Reply reply = new Reply();
        switch (type) {
            case CHOICE:
                List<Boolean> choices = getChoiceData(elements);
                reply.setChoices(choices);
                break;
            case STRING:
                String text = getStringData(elements);
                reply.setText(text);
                break;
            case FREE_ANSWER:
                text = getStringData(elements);
                reply.setText(text);
                reply.setAttachments(emptyList());
                break;
            case NUMBER:
                String number = getStringData(elements);
                reply.setNumber(number);
                break;
            case DATASET:
                String dataset;
                if (data == null) {
                    dataset = getStringData(elements);
                } else {
                    dataset = data;
                }
                reply.setFile(dataset);
                break;
            case TABLE:
                List tableChoices = getChoices(elements);
                reply.setChoices(tableChoices);
                break;
            case FILL_BLANKS:
                List<String> blanks = getBlanks(elements);
                reply.setBlanks(blanks);
                break;
            case SORTING:
            case MATCHING:
                List<Integer> ordering = getOrderingData(elements);
                reply.setOrdering(ordering);
                break;
            case MATH:
                String formula = getStringData(elements);
                reply.setFormula(formula);
                break;
            default:
                logger.warn("Unknown step type tried sending: " + type);
                return null;
        }
        stepNode.setLastReply(reply);
        return reply;
    }

    private static void forEachInputElement(@NotNull Elements elements, Consumer<HTMLInputElement> consumer) {
        for (Node node : elements) {
            if (node instanceof HTMLInputElement) {
                HTMLInputElement element = (HTMLInputElement) node;
                consumer.accept(element);
                element.setDisabled(true);
            }
        }
    }

    private static void disableAllInputs(@NotNull Elements elements) {
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
    private static List<Boolean> getChoiceData(@NotNull Elements elements) {
        List<Boolean> choices = new ArrayList<>();

        forEachInputElement(elements, element -> {
            if ("option".equals(element.getName())) {
                choices.add(element.getChecked());
            }
        });

        return choices;
    }

    @NotNull
    private static List<Integer> getOrderingData(@NotNull Elements elements) {
        List<Integer> ordering = new ArrayList<>();

        forEachInputElement(elements, element -> {
            if ("index".equals(element.getName())) {
                String indexAttr = element.getValue();
                ordering.add(Integer.valueOf(indexAttr));
            }
        });
        return ordering;
    }

    private static String getStringData(@NotNull Elements elements) {
        disableAllInputs(elements);
        return elements.getInputValue("text");
    }

    @NotNull
    private static List<Choice> getChoices(@NotNull Elements elements) {
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

    private static List<String> getBlanks(@NotNull Elements elements) {
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
}
