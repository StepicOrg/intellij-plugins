package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.StudyObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public abstract class Node<
        D extends StudyObject,
        C extends StudyNode<DC, CC>,
        DC extends StudyObject,
        CC extends Node> implements StudyNode<D, C> {
    private static final Logger logger = Logger.getInstance(Node.class);
    private StudyNode parent;
    private D data;
    private List<C> children;
    private boolean wasDeleted;

    Node() {
    }

    Node(@NotNull D data, @Nullable ProgressIndicator indicator) {
        setData(data);
        init(null, true, indicator);
    }

    @Nullable
    private StudyNode getLastNode() {
        List<C> children = getChildren();

        for (int i = children.size() - 1; i > 0; i--) {
            C child = children.get(i);
            if (!child.getWasDeleted()) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    private StudyNode getFirstNode() {
        for (C child : getChildren()) {
            if (!child.getWasDeleted()) {
                return child;
            }
        }

        return null;
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
            if (!item.getWasDeleted() && item.getPosition() < position) {
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
            if (!item.getWasDeleted() && item.getPosition() > position) {
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

    public void setParent(@Nullable StudyNode parent) {
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
        for (C child : getChildren()) {
            if (child.getId() == id) {
                return child;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public C getChildByPosition(int position) {
        for (C child : getChildren()) {
            if (child.getPosition() == position) {
                return child;
            }
        }

        return null;
    }

    @NotNull
    private Map<Long, C> getMapNodes() {
        HashMap<Long, C> mapNodes = new HashMap<>();
        getChildren().forEach(node -> mapNodes.put(node.getId(), node));
        return mapNodes;
    }

    private void sortChildren() {
        getChildren().sort(StudyNodeComparator.getInstance());
    }

    @Override
    public List<C> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }

        return children;
    }

    protected abstract List<DC> getChildDataList();

    @Override
    public void init(
            @Nullable StudyNode parent,
            boolean isRestarted,
            @Nullable ProgressIndicator indicator) {
        Map<Long, C> mapNodes = getMapNodes();
        List<C> needInit = getChildren();
        setChildrenDeletedFlag();

        for (DC data : getChildDataList()) {
            C child = mapNodes.get(data.getId());
            if (child != null) {
                child.setData(data);
                child.setWasDeleted(wasDeleted);
            } else {
                C item;
                try {
                    item = getChildClass().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.warn("Can't get new instance for child", e);
                    break;
                }
                item.setData(data);
                item.setWasDeleted(wasDeleted);
                item.init(this, isRestarted, indicator);
                if (item.canBeLeaf() || !item.isLeaf()) {
                    getChildren().add(item);
                }
            }
        }

        sortChildren();
        setParent(parent);

        for (StudyNode child : needInit) {
            child.init(this, isRestarted, indicator);
        }
    }

    private void setChildrenDeletedFlag() {
        getChildren().forEach(child -> child.setWasDeleted(true));
    }

    @Override
    public void reloadData(@NotNull ProgressIndicator indicator) {
        loadData(data.getId());
        init(indicator);
    }

    protected abstract void loadData(long id);

    protected abstract Class<C> getChildClass();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node<?, ?, ?, ?> node = (Node<?, ?, ?, ?>) o;

        //noinspection SimplifiableIfStatement
        if (parent != null ? !parent.equals(node.parent) : node.parent != null) return false;
        return children != null ? children.equals(node.children) : node.children == null;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }

    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (StudyNode child : getChildren()) {
            if (child.getStatus() != StudyStatus.SOLVED)
                return StudyStatus.UNCHECKED;
        }

        return StudyStatus.SOLVED;
    }

    @Nullable
    @Override
    public D getData() {
        if (data == null) {
            try {
                data = getDataClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.warn("Can't create data instance: " + getDataClass().getName());
                return null;
            }
        }
        return data;
    }

    @Override
    public void setData(@Nullable D data) {
        this.data = data;
    }

    protected abstract Class<D> getDataClass();

    @Override
    public long getId() {
        StudyObject data = getData();
        return data != null ? data.getId() : 0;
    }

    public void setId(long id) {
        StudyObject data = getData();
        if (data != null) {
            data.setId(id);
        }
    }

    @Override
    public boolean canBeLeaf() {
        return false;
    }

    @Override
    public int getPosition() {
        StudyObject data = getData();
        return data != null ? data.getPosition() : 0;
    }

    @NotNull
    @Override
    public String getName() {
        StudyObject data = getData();
        return data != null ? data.getTitle() : "";
    }

    @Override
    public boolean getWasDeleted() {
        return wasDeleted;
    }

    @Override
    public void setWasDeleted(boolean wasDeleted) {
        this.wasDeleted = wasDeleted;
    }
}
