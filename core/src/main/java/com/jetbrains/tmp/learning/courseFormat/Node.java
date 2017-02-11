package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public abstract class Node<C extends StudyNode, D> implements StudyNode<C> {
    private StudyNode parent;
    private Map<Long, C> mapNodes;

    public Node() {
    }

    public Node(@NotNull final StudyNode parent, @NotNull D data) {
        setData(data);
        init(parent, true, null);
    }

    public Node(@NotNull D data, @Nullable ProgressIndicator indicator) {
        setData(data);
        init(null, true, indicator);
    }

    @Nullable
    private StudyNode getLastNode() {
        int stepsCount = getChildren().size();
        if (stepsCount == 0) {
            return null;
        }
        return getChildren().get(stepsCount - 1);
    }

    @Nullable
    private StudyNode getFirstNode() {
        List<C> children = getChildren();
        if (children.size() == 0) {
            return null;
        }

        return children.get(0);
    }

    @Nullable
    @Override
    public StudyNode getPrevChild(@Nullable StudyNode current) {
        List<C> children = getChildren();
        if (current == null) {
            return getLastNode();
        }

        int position = current.getPosition();

        for (int i = children.size() - 1; i >= 0; i--) {
            StudyNode item = children.get(i);
            if (item.getPosition() < position) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public StudyNode getNextChild(@Nullable StudyNode current) {
        List<C> children = getChildren();
        if (current == null) {
            return getFirstNode();
        }

        int position = current.getPosition();
        for (StudyNode item : children) {
            if (item.getPosition() > position) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    @Nullable
    @Override
    public StudyNode getParent() {
        return parent;
    }

    public void setParent(StudyNode parent) {
        this.parent = parent;
    }

    @NotNull
    @Override
    public String getPath() {
        if (parent != null) {
            if (parent.getPath().isEmpty()) {
                return getDirectory();
            }
            return parent.getPath() + "/" + getDirectory();
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDirectory() {
        if (parent != null) {
            return getDirectoryPrefix() + getId();
        } else {
            return "";
        }
    }

    @NotNull
    String getDirectoryPrefix() {
        return "";
    }

    @Nullable
    @Override
    public C getChildById(long id) {
        return getMapNodes().get(id);
    }

    private Map<Long, C> getMapNodes() {
        if (mapNodes == null) {
            mapNodes = new HashMap<>();
            getChildren().forEach(node -> mapNodes.put(node.getId(), node));
        }
        return mapNodes;
    }

    void clearMapNodes() {
        mapNodes = null;
    }

    void sortChildren() {
        getChildren().sort(StudyNodeComparator.getInstance());
    }

    protected abstract void init(
            @Nullable final StudyNode parent,
            boolean isRestarted,
            @Nullable ProgressIndicator indicator);

    public void init(boolean isRestarted, @Nullable ProgressIndicator indicator) {
        init(null, isRestarted, indicator);
    }

    @NotNull
    public abstract D getData();

    public abstract void setData(@Nullable D data);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node<?, ?> node = (Node<?, ?>) o;

        return getData().equals(node.getData());
    }

    @Override
    public int hashCode() {
        return getData().hashCode();
    }
}
