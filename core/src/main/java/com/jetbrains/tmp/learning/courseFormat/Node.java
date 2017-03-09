package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.objects.progresses.Progresses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jetbrains.tmp.learning.courseFormat.StudyStatus.SOLVED;

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
    @XStreamOmitField
    private StudyStatus status;

    Node() {
    }

    Node(@NotNull D data, @Nullable ProgressIndicator indicator) {
        setData(data);
        init(null, indicator);
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
    public void init(@Nullable StudyNode parent, @Nullable ProgressIndicator indicator) {
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
                item.init(this, indicator);
                if (item.canBeLeaf() || !item.isLeaf()) {
                    getChildren().add(item);
                }
            }
        }

        sortChildren();
        setParent(parent);

        for (StudyNode child : needInit) {
            child.init(this, indicator);
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
    public boolean isUnknownStatus() {
        return status == null;
    }

    @NotNull
    @Override
    public StudyStatus getStatus() {
        if (status == null) {
            status = StudyStatus.UNCHECKED;

            try {
                Map<String, StudyNode> progressMap = new HashMap<>();
                getChildren().stream()
                        .filter(StudyNode::isUnknownStatus)
                        .forEach(child -> {
                            DC data = child.getData();
                            if (data != null) {
                                progressMap.put(data.getProgress(), child);
                            }
                        });
                D data = getData();
                if (data != null) {
                    String progressId = data.getProgress();
                    if (progressId != null) {
                        progressMap.put(progressId, this);
                    }

                    Set<String> progressIds = progressMap.keySet();

                    if (progressIds.size() != 0) {
                        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

                        Progresses progresses = stepikApiClient.progresses()
                                .get()
                                .id(progressIds.toArray(new String[progressIds.size()]))
                                .execute();

                        progresses.getItems().forEach(progress -> {
                            String id = progress.getId();
                            StudyNode node = progressMap.get(id);
                            if (progress.isPassed()) {
                                node.setRawStatus(SOLVED);
                            }
                        });
                    }
                }
            } catch (StepikClientException e) {
                logger.warn(e);
            }
        }

        return status;
    }

    @Override
    public void setStatus(@Nullable StudyStatus status) {
        if (status == SOLVED && this.status != status) {
            this.status = SOLVED;

            StudyNode parent = getParent();

            while (parent != null) {
                parent.setRawStatus(null);
                parent = parent.getParent();
            }
        }
    }

    @Override
    public void setRawStatus(@Nullable StudyStatus status) {
        this.status = status;
    }

    @Override
    public void passed() {
        setStatus(SOLVED);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node<?, ?, ?, ?> node = (Node<?, ?, ?, ?>) o;

        if (wasDeleted != node.wasDeleted) return false;
        if (data != null ? !data.equals(node.data) : node.data != null) return false;
        //noinspection SimplifiableIfStatement
        if (children != null ? !children.equals(node.children) : node.children != null) return false;
        return status == node.status;
    }

    @Override
    public int hashCode() {
        int result = (data != null ? data.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (wasDeleted ? 1 : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
