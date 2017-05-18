package org.stepik.core.ui;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLTextAreaElement;

import java.util.Iterator;

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